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

import com.transportista.guias.dto.GuiaRequest;
import com.transportista.guias.dto.GuiaResponse;
import com.transportista.guias.dto.S3UploadResponse;
import com.transportista.guias.service.GuiaDespachoService;

@RestController
@RequestMapping("/api/guias")
public class GuiaDespachoController {

    private final GuiaDespachoService service;

    public GuiaDespachoController(GuiaDespachoService service) {
        this.service = service;
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
            @RequestParam String transportista,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
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

    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargar(@PathVariable Long id) {
        byte[] contenido = service.descargar(id);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("guia-" + id + ".txt").build().toString())
                .body(contenido);
    }
}
