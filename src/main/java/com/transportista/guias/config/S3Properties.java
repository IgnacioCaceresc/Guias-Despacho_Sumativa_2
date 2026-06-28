package com.transportista.guias.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.s3")
public record S3Properties(
        boolean enabled,
        String bucket,
        String region
) {
}
