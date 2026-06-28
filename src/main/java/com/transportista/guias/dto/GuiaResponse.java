package com.transportista.guias.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.transportista.guias.domain.EstadoGuia;
import com.transportista.guias.domain.GuiaDespacho;

public record GuiaResponse(
        Long id,
        String numeroPedido,
        String transportista,
        LocalDate fechaDespacho,
        String destinatario,
        String direccionDestino,
        String comunaDestino,
        String ciudadDestino,
        Double pesoKg,
        Integer cantidadBultos,
        String observaciones,
        EstadoGuia estado,
        String s3Key,
        Instant creadaEn,
        Instant actualizadaEn
) {
    public static GuiaResponse fromEntity(GuiaDespacho guia) {
        return new GuiaResponse(
                guia.getId(),
                guia.getNumeroPedido(),
                guia.getTransportista(),
                guia.getFechaDespacho(),
                guia.getDestinatario(),
                guia.getDireccionDestino(),
                guia.getComunaDestino(),
                guia.getCiudadDestino(),
                guia.getPesoKg(),
                guia.getCantidadBultos(),
                guia.getObservaciones(),
                guia.getEstado(),
                guia.getS3Key(),
                guia.getCreadaEn(),
                guia.getActualizadaEn()
        );
    }
}
