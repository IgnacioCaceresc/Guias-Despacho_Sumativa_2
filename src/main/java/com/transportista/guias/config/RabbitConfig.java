package com.transportista.guias.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitConfig {

    @Bean
    Queue colaGuias(RabbitProperties properties) {
        return new Queue(properties.colaGuias(), true);
    }

    @Bean
    Queue colaErrores(RabbitProperties properties) {
        return new Queue(properties.colaErrores(), true);
    }

    @Bean
    MessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
