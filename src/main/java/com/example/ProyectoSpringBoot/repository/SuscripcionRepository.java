package com.example.ProyectoSpringBoot.repository;

import com.example.ProyectoSpringBoot.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ProyectoSpringBoot.model.EstadoSuscripcion;
import java.time.LocalDate;
import java.util.List;

import com.example.ProyectoSpringBoot.model.Usuario;
import java.util.Optional;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    List<Suscripcion> findByEstadoAndFechaFin(EstadoSuscripcion estado, LocalDate fechaFin);

    Optional<Suscripcion> findByUsuarioAndEstado(Usuario usuario, EstadoSuscripcion estado);

    List<Suscripcion> findByUsuario(Usuario usuario);
}
