package com.example.ProyectoSpringBoot.model;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "usuarios")
@Audited
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String rol; // ROLE_ADMIN, ROLE_USER

    // Relación con Perfil
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "perfil_id")
    private Perfil perfil;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<Suscripcion> suscripciones;

    // Getters y Setters

    public java.util.List<Suscripcion> getSuscripciones() {
        return suscripciones;
    }

    public void setSuscripciones(java.util.List<Suscripcion> suscripciones) {
        this.suscripciones = suscripciones;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

}