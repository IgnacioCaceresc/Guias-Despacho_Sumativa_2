package com.transportista.guias.domain;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "guias_despacho")
public class GuiaDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGuia estado = EstadoGuia.GENERADA;

    private String archivoLocal;

    private String s3Key;

    private Instant creadaEn;

    private Instant actualizadaEn;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        creadaEn = now;
        actualizadaEn = now;
    }

    @PreUpdate
    void preUpdate() {
        actualizadaEn = Instant.now();
    }

    public Long getId() {
        return id;
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

    public EstadoGuia getEstado() {
        return estado;
    }

    public void setEstado(EstadoGuia estado) {
        this.estado = estado;
    }

    public String getArchivoLocal() {
        return archivoLocal;
    }

    public void setArchivoLocal(String archivoLocal) {
        this.archivoLocal = archivoLocal;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public Instant getCreadaEn() {
        return creadaEn;
    }

    public Instant getActualizadaEn() {
        return actualizadaEn;
    }
}
