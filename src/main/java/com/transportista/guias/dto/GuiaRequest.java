package com.transportista.guias.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GuiaRequest(
        @NotBlank String numeroPedido,
        @NotBlank String transportista,
        @NotNull LocalDate fechaDespacho,
        @NotBlank String destinatario,
        @NotBlank String direccionDestino,
        @NotBlank String comunaDestino,
        @NotBlank String ciudadDestino,
        @NotNull @DecimalMin("0.1") Double pesoKg,
        @NotNull @Min(1) Integer cantidadBultos,
        String observaciones
) {
}
