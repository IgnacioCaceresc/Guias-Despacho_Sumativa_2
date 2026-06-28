package com.transportista.guias.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transportista.guias.domain.EstadoGuia;
import com.transportista.guias.domain.GuiaDespacho;

public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {

    List<GuiaDespacho> findByTransportistaIgnoreCaseAndFechaDespachoAndEstadoNot(
            String transportista,
            LocalDate fechaDespacho,
            EstadoGuia estado
    );
}
