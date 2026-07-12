package com.transportista.guias.service;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transportista.guias.config.RabbitProperties;
import com.transportista.guias.domain.GuiaDespacho;
import com.transportista.guias.domain.GuiaDespachoProcesada;
import com.transportista.guias.dto.ConsumoColaResponse;
import com.transportista.guias.dto.GuiaErrorMensaje;
import com.transportista.guias.dto.GuiaMensaje;
import com.transportista.guias.repository.GuiaDespachoProcesadaRepository;

@Service
public class GuiaColaService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties properties;
    private final GuiaDespachoProcesadaRepository procesadaRepository;
    private final ObjectMapper objectMapper;

    public GuiaColaService(
            RabbitTemplate rabbitTemplate,
            RabbitProperties properties,
            GuiaDespachoProcesadaRepository procesadaRepository,
            ObjectMapper objectMapper
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
        this.procesadaRepository = procesadaRepository;
        this.objectMapper = objectMapper;
    }

    public boolean publicarGuia(GuiaDespacho guia, String operacion) {
        GuiaMensaje mensaje = GuiaMensaje.fromEntity(guia, operacion);
        try {
            rabbitTemplate.convertAndSend(properties.colaGuias(), mensaje);
            return true;
        } catch (AmqpException ex) {
            try {
                rabbitTemplate.convertAndSend(properties.colaErrores(), GuiaErrorMensaje.of(mensaje, ex));
            } catch (AmqpException ignored) {
                // Si RabbitMQ completo no esta disponible, la guia queda marcada como ERROR_COLA.
            }
            return false;
        }
    }

    @Transactional
    public ConsumoColaResponse consumirUnaGuia() {
        Object payload = rabbitTemplate.receiveAndConvert(properties.colaGuias());
        if (payload == null) {
            throw new IllegalStateException("No hay mensajes disponibles en la cola de guias");
        }
        GuiaMensaje mensaje = convertir(payload);
        GuiaDespachoProcesada procesada = procesadaRepository.save(toProcesada(mensaje));
        return ConsumoColaResponse.fromEntity(procesada);
    }

    @Transactional
    public java.util.List<ConsumoColaResponse> consumirGuias(int cantidad) {
        java.util.List<ConsumoColaResponse> procesadas = new java.util.ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Object payload = rabbitTemplate.receiveAndConvert(properties.colaGuias());
            if (payload == null) {
                break;
            }
            GuiaDespachoProcesada procesada = procesadaRepository.save(toProcesada(convertir(payload)));
            procesadas.add(ConsumoColaResponse.fromEntity(procesada));
        }
        return procesadas;
    }

    private GuiaMensaje convertir(Object payload) {
        if (payload instanceof GuiaMensaje mensaje) {
            return mensaje;
        }
        return objectMapper.convertValue(payload, GuiaMensaje.class);
    }

    private GuiaDespachoProcesada toProcesada(GuiaMensaje mensaje) {
        GuiaDespachoProcesada procesada = new GuiaDespachoProcesada();
        procesada.setGuiaOriginalId(mensaje.guiaOriginalId());
        procesada.setNumeroPedido(mensaje.numeroPedido());
        procesada.setTransportista(mensaje.transportista());
        procesada.setFechaDespacho(mensaje.fechaDespacho());
        procesada.setDestinatario(mensaje.destinatario());
        procesada.setDireccionDestino(mensaje.direccionDestino());
        procesada.setComunaDestino(mensaje.comunaDestino());
        procesada.setCiudadDestino(mensaje.ciudadDestino());
        procesada.setPesoKg(mensaje.pesoKg());
        procesada.setCantidadBultos(mensaje.cantidadBultos());
        procesada.setObservaciones(mensaje.observaciones());
        procesada.setOperacion(mensaje.operacion());
        return procesada;
    }
}
