package com.example.ProyectoSpringBoot.model;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Entity
@Table(name = "suscripciones")
@Audited // Para auditoría con Envers
public class Suscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metodo_pago_id")
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    private EstadoSuscripcion estado; // ACTIVA, CANCELADA, MOROSA

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Getters y Setters

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Plan getPlan() {
        return plan;
    }

    public EstadoSuscripcion getEstado() {
        return estado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public void setEstado(EstadoSuscripcion estado) {
        this.estado = estado;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}