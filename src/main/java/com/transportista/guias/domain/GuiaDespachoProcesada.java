package com.transportista.guias.domain;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "guias_despacho_procesadas")
public class GuiaDespachoProcesada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long guiaOriginalId;

    @Column(nullable = false)
    private String numeroPedido;

    @Column(nullable = false)
    private String transportista;

    @Column(nullable = false)
    private LocalDate fechaDespacho;

    @Column(nullable = false)
    private String destinatario;

    @Column(nullable = false)
    private String direccionDestino;

    @Column(nullable = false)
    private String comunaDestino;

    @Column(nullable = false)
    private String ciudadDestino;

    @Column(nullable = false)
    private Double pesoKg;

    @Column(nullable = false)
    private Integer cantidadBultos;

    @Column(length = 1200)
    private String observaciones;

    @Column(nullable = false)
    private String operacion;

    private Instant procesadaEn;

    @PrePersist
    void prePersist() {
        procesadaEn = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getGuiaOriginalId() {
        return guiaOriginalId;
    }

    public void setGuiaOriginalId(Long guiaOriginalId) {
        this.guiaOriginalId = guiaOriginalId;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getTransportista() {
        return transportista;
    }

    public void setTransportista(String transportista) {
        this.transportista = transportista;
    }

    public LocalDate getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(LocalDate fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }

    public String getComunaDestino() {
        return comunaDestino;
    }

    public void setComunaDestino(String comunaDestino) {
        this.comunaDestino = comunaDestino;
    }

    public String getCiudadDestino() {
        return ciudadDestino;
    }

    public void setCiudadDestino(String ciudadDestino) {
        this.ciudadDestino = ciudadDestino;
    }

    public Double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(Double pesoKg) {
        this.pesoKg = pesoKg;
    }

    public Integer getCantidadBultos() {
        return cantidadBultos;
    }

    public void setCantidadBultos(Integer cantidadBultos) {
        this.cantidadBultos = cantidadBultos;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public Instant getProcesadaEn() {
        return procesadaEn;
    }
}
