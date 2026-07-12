package com.transportista.guias.controller;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.transportista.guias.dto.ConsumoColaResponse;
import com.transportista.guias.dto.GuiaRequest;
import com.transportista.guias.dto.GuiaResponse;
import com.transportista.guias.dto.S3UploadResponse;
import com.transportista.guias.service.GuiaColaService;
import com.transportista.guias.service.GuiaDespachoService;

@RestController
@RequestMapping("/api/guias")
public class GuiaDespachoController {

    private final GuiaDespachoService service;
    private final GuiaColaService guiaColaService;

    public GuiaDespachoController(GuiaDespachoService service, GuiaColaService guiaColaService) {
        this.service = service;
        this.guiaColaService = guiaColaService;
    }

    @PostMapping
    public ResponseEntity<GuiaResponse> crear(
            @Valid @RequestBody GuiaRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        GuiaResponse response = service.crear(request);
        return ResponseEntity.created(uriBuilder.path("/api/guias/{id}").build(response.id())).body(response);
    }

    @GetMapping
    public List<GuiaResponse> consultar(
            @RequestParam(required = false) String transportista,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return service.consultar(transportista, fecha);
    }

    @PutMapping("/{id}")
    public GuiaResponse actualizar(@PathVariable Long id, @Valid @RequestBody GuiaRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/subir-s3")
    public S3UploadResponse subirS3(@PathVariable Long id) {
        return service.subirS3(id);
    }

    @PostMapping("/cola/consumir")
    public List<ConsumoColaResponse> consumirCola(@RequestParam(defaultValue = "10") Integer cantidad) {
        int cantidadNormalizada = Math.max(1, Math.min(cantidad, 100));
        return guiaColaService.consumirGuias(cantidadNormalizada);
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargar(@PathVariable Long id) {
        byte[] contenido = service.descargar(id);
        String numeroPedido = service.obtenerEntidad(id).getNumeroPedido();
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(numeroPedido + ".txt").build().toString())
                .body(contenido);
    }
}
