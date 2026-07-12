package com.transportista.guias.dto;

import java.time.Instant;

public record GuiaErrorMensaje(
        GuiaMensaje guia,
        String error,
        Instant registradaEn
) {
    public static GuiaErrorMensaje of(GuiaMensaje guia, Exception ex) {
        return new GuiaErrorMensaje(guia, ex.getMessage(), Instant.now());
    }
}
