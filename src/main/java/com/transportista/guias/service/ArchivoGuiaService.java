package com.transportista.guias.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.transportista.guias.domain.GuiaDespacho;

@Service
public class ArchivoGuiaService {

    private final Path storagePath;

    public ArchivoGuiaService(@Value("${app.storage.local-path}") Path storagePath) {
        this.storagePath = storagePath;
    }

    public Path generarArchivo(GuiaDespacho guia) {
        try {
            Files.createDirectories(storagePath);
            String fileName = guia.getNumeroPedido() + ".txt";
            Path file = storagePath.resolve(fileName);
            Files.writeString(file, contenidoGuia(guia), StandardCharsets.UTF_8);
            return file;
        } catch (IOException ex) {
            throw new IllegalStateException("No fue posible generar el archivo de la guia", ex);
        }
    }

    public byte[] leerArchivo(GuiaDespacho guia) {
        try {
            Path file = guia.getArchivoLocal() == null ? generarArchivo(guia) : Path.of(guia.getArchivoLocal());
            if (!Files.exists(file)) {
                file = generarArchivo(guia);
            }
            return Files.readAllBytes(file);
        } catch (IOException ex) {
            throw new IllegalStateException("No fue posible leer el archivo de la guia", ex);
        }
    }

    private String contenidoGuia(GuiaDespacho guia) {
        return """
                GUIA DE DESPACHO
                =================
                ID: %s
                Pedido: %s
                Transportista: %s
                Fecha despacho: %s
                Destinatario: %s
                Direccion: %s, %s, %s
                Peso: %.2f kg
                Bultos: %d
                Observaciones: %s
                Estado: %s
                """.formatted(
                guia.getId(),
                guia.getNumeroPedido(),
                guia.getTransportista(),
                guia.getFechaDespacho().format(DateTimeFormatter.ISO_DATE),
                guia.getDestinatario(),
                guia.getDireccionDestino(),
                guia.getComunaDestino(),
                guia.getCiudadDestino(),
                guia.getPesoKg(),
                guia.getCantidadBultos(),
                guia.getObservaciones() == null ? "Sin observaciones" : guia.getObservaciones(),
                guia.getEstado()
        );
    }
}
