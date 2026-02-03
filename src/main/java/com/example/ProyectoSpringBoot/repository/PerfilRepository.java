package com.example.ProyectoSpringBoot.repository;

import com.example.ProyectoSpringBoot.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
}
