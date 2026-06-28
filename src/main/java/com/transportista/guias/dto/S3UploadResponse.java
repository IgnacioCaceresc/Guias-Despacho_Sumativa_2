package com.transportista.guias.dto;

public record S3UploadResponse(
        Long guiaId,
        String bucket,
        String key,
        boolean s3Real,
        String mensaje
) {
}
