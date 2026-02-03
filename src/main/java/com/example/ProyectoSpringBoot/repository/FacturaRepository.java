package com.example.ProyectoSpringBoot.repository;

import com.example.ProyectoSpringBoot.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
}
