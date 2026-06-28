package com.transportista.guias.service;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.transportista.guias.domain.EstadoGuia;
import com.transportista.guias.domain.GuiaDespacho;
import com.transportista.guias.dto.GuiaRequest;
import com.transportista.guias.dto.GuiaResponse;
import com.transportista.guias.dto.S3UploadResponse;
import com.transportista.guias.repository.GuiaDespachoRepository;

@Service
public class GuiaDespachoService {

    private final GuiaDespachoRepository repository;
    private final ArchivoGuiaService archivoGuiaService;
    private final S3StorageService s3StorageService;

    public GuiaDespachoService(
            GuiaDespachoRepository repository,
            ArchivoGuiaService archivoGuiaService,
            S3StorageService s3StorageService
    ) {
        this.repository = repository;
        this.archivoGuiaService = archivoGuiaService;
        this.s3StorageService = s3StorageService;
    }

    @Transactional
    public GuiaResponse crear(GuiaRequest request) {
        GuiaDespacho guia = new GuiaDespacho();
        aplicarDatos(guia, request);
        GuiaDespacho guardada = repository.save(guia);
        Path archivo = archivoGuiaService.generarArchivo(guardada);
        guardada.setArchivoLocal(archivo.toString());
        return GuiaResponse.fromEntity(repository.save(guardada));
    }

    @Transactional(readOnly = true)
    public List<GuiaResponse> consultar(String transportista, LocalDate fecha) {
        if (transportista == null && fecha == null) {
            return repository.findAll().stream()
                    .filter(g -> g.getEstado() != EstadoGuia.ELIMINADA)
                    .map(GuiaResponse::fromEntity)
                    .toList();
        }
        return repository.findByTransportistaIgnoreCaseAndFechaDespachoAndEstadoNot(
                        transportista,
                        fecha,
                        EstadoGuia.ELIMINADA
                )
                .stream()
                .map(GuiaResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public GuiaDespacho obtenerEntidad(Long id) {
        GuiaDespacho guia = repository.findById(id).orElseThrow(() -> new GuiaNotFoundException(id));
        if (guia.getEstado() == EstadoGuia.ELIMINADA) {
            throw new GuiaNotFoundException(id);
        }
        return guia;
    }

    @Transactional
    public GuiaResponse actualizar(Long id, GuiaRequest request) {
        GuiaDespacho guia = obtenerEntidad(id);
        aplicarDatos(guia, request);
        Path archivo = archivoGuiaService.generarArchivo(guia);
        guia.setArchivoLocal(archivo.toString());
        return GuiaResponse.fromEntity(repository.save(guia));
    }

    @Transactional
    public void eliminar(Long id) {
        GuiaDespacho guia = obtenerEntidad(id);
        guia.setEstado(EstadoGuia.ELIMINADA);
        repository.save(guia);
    }

    @Transactional
    public S3UploadResponse subirS3(Long id) {
        GuiaDespacho guia = obtenerEntidad(id);
        Path archivo = guia.getArchivoLocal() == null
                ? archivoGuiaService.generarArchivo(guia)
                : Path.of(guia.getArchivoLocal());
        S3UploadResponse response = s3StorageService.subir(guia, archivo);
        guia.setS3Key(response.key());
        guia.setEstado(EstadoGuia.SUBIDA_S3);
        repository.save(guia);
        return response;
    }

    @Transactional(readOnly = true)
    public byte[] descargar(Long id) {
        GuiaDespacho guia = obtenerEntidad(id);
        return archivoGuiaService.leerArchivo(guia);
    }

    private void aplicarDatos(GuiaDespacho guia, GuiaRequest request) {
        guia.setNumeroPedido(request.numeroPedido());
        guia.setTransportista(request.transportista());
        guia.setFechaDespacho(request.fechaDespacho());
        guia.setDestinatario(request.destinatario());
        guia.setDireccionDestino(request.direccionDestino());
        guia.setComunaDestino(request.comunaDestino());
        guia.setCiudadDestino(request.ciudadDestino());
        guia.setPesoKg(request.pesoKg());
        guia.setCantidadBultos(request.cantidadBultos());
        guia.setObservaciones(request.observaciones());
    }
}
