package com.example.ProyectoSpringBoot.repository;

import com.example.ProyectoSpringBoot.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findByFechaEmisionBetween(LocalDate startDate, LocalDate endDate);

    List<Factura> findByCantidadGreaterThanEqual(BigDecimal amount);
}
