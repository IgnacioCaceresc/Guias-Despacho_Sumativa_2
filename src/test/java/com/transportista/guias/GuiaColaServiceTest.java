package com.transportista.guias;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.transportista.guias.config.RabbitProperties;
import com.transportista.guias.domain.GuiaDespacho;
import com.transportista.guias.dto.GuiaErrorMensaje;
import com.transportista.guias.dto.GuiaMensaje;
import com.transportista.guias.service.GuiaColaService;

class GuiaColaServiceTest {

    private final RabbitProperties properties = new RabbitProperties("guias.cola1", "guias.cola2.errores");

    @Test
    void enviaMensajeAColaPrincipal() {
        FakeRabbitTemplate rabbitTemplate = new FakeRabbitTemplate();
        GuiaColaService service = new GuiaColaService(rabbitTemplate, properties, null, new ObjectMapper());

        service.publicarGuia(guia(), "CREACION");

        assertThat(rabbitTemplate.sentMessages)
                .singleElement()
                .satisfies(message -> {
                    assertThat(message.routingKey()).isEqualTo("guias.cola1");
                    assertThat(message.payload()).isInstanceOf(GuiaMensaje.class);
                });
    }

    @Test
    void enviaMensajeAColaErroresSiFallaColaPrincipal() {
        FakeRabbitTemplate rabbitTemplate = new FakeRabbitTemplate();
        rabbitTemplate.failMainQueue = true;
        GuiaColaService service = new GuiaColaService(rabbitTemplate, properties, null, new ObjectMapper());

        service.publicarGuia(guia(), "CREACION");

        assertThat(rabbitTemplate.sentMessages)
                .singleElement()
                .satisfies(message -> {
                    assertThat(message.routingKey()).isEqualTo("guias.cola2.errores");
                    assertThat(message.payload()).isInstanceOf(GuiaErrorMensaje.class);
                });
    }

    private GuiaDespacho guia() {
        GuiaDespacho guia = new GuiaDespacho();
        guia.setNumeroPedido("PED-1001");
        guia.setTransportista("Transportes Norte");
        guia.setFechaDespacho(LocalDate.of(2026, 6, 28));
        guia.setDestinatario("Cliente Demo");
        guia.setDireccionDestino("Av. Siempre Viva 123");
        guia.setComunaDestino("Santiago");
        guia.setCiudadDestino("Santiago");
        guia.setPesoKg(12.5);
        guia.setCantidadBultos(3);
        guia.setObservaciones("Entrega en horario de oficina");
        return guia;
    }

    static class FakeRabbitTemplate extends RabbitTemplate {
        private final List<SentMessage> sentMessages = new ArrayList<>();
        private boolean failMainQueue;

        @Override
        public void convertAndSend(String routingKey, Object object) throws AmqpException {
            if (failMainQueue && "guias.cola1".equals(routingKey)) {
                throw new AmqpException("cola principal no disponible");
            }
            sentMessages.add(new SentMessage(routingKey, object));
        }
    }

    record SentMessage(String routingKey, Object payload) {
    }
}
