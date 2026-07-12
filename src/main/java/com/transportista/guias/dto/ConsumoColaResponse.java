package com.transportista.guias.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.transportista.guias.domain.GuiaDespachoProcesada;

public record ConsumoColaResponse(
        Long id,
        Long guiaOriginalId,
        String numeroPedido,
        String transportista,
        LocalDate fechaDespacho,
        String operacion,
        Instant procesadaEn
) {
    public static ConsumoColaResponse fromEntity(GuiaDespachoProcesada guia) {
        return new ConsumoColaResponse(
                guia.getId(),
                guia.getGuiaOriginalId(),
                guia.getNumeroPedido(),
                guia.getTransportista(),
                guia.getFechaDespacho(),
                guia.getOperacion(),
                guia.getProcesadaEn()
        );
    }
}
