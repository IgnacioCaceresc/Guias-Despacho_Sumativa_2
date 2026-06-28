package com.transportista.guias.service;

public class GuiaNotFoundException extends RuntimeException {

    public GuiaNotFoundException(Long id) {
        super("No existe una guia de despacho con id " + id);
    }
}
