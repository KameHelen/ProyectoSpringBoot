package com.example.ProyectoSpringBoot.repository;

import com.example.ProyectoSpringBoot.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}