package com.transportista.guias.service;

import java.nio.file.Path;

import org.springframework.stereotype.Service;

import com.transportista.guias.config.S3Properties;
import com.transportista.guias.domain.GuiaDespacho;
import com.transportista.guias.dto.S3UploadResponse;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Properties properties;

    public S3StorageService(S3Client s3Client, S3Properties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    public S3UploadResponse subir(GuiaDespacho guia, Path archivo) {
        String key = "guias/%s/%s/%s.txt".formatted(
                guia.getFechaDespacho(),
                guia.getTransportista().replaceAll("\\s+", "-").toLowerCase(),
                guia.getNumeroPedido()
        );

        if (!properties.enabled()) {
            return new S3UploadResponse(
                    guia.getId(),
                    properties.bucket(),
                    key,
                    false,
                    "S3 deshabilitado: subida simulada correctamente"
            );
        }

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(key)
                .contentType("text/plain")
                .build();
        s3Client.putObject(request, RequestBody.fromFile(archivo));

        return new S3UploadResponse(
                guia.getId(),
                properties.bucket(),
                key,
                true,
                "Guia subida correctamente a S3"
        );
    }
}
