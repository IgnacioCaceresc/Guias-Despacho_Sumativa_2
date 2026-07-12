package com.transportista.guias.dto;

import java.time.LocalDate;

import com.transportista.guias.domain.GuiaDespacho;

public record GuiaMensaje(
        Long guiaOriginalId,
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
        String operacion
) {
    public static GuiaMensaje fromEntity(GuiaDespacho guia, String operacion) {
        return new GuiaMensaje(
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
                operacion
        );
    }
}
