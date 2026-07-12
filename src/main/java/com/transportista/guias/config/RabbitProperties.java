package com.transportista.guias.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbit")
public record RabbitProperties(
        String colaGuias,
        String colaErrores
) {
}
