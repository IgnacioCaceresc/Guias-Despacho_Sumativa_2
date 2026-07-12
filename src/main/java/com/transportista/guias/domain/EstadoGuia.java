package com.transportista.guias.domain;

public enum EstadoGuia {
    GENERADA,
    ENVIADA_COLA,
    ERROR_COLA,
    PROCESADA,
    SUBIDA_S3,
    ELIMINADA
}
