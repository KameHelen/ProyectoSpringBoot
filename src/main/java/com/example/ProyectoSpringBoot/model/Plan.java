package com.example.ProyectoSpringBoot.model;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Entity
@Table(name = "planes")
@Audited
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // BASIC, PREMIUM, ENTERPRISE
    private BigDecimal precioMensual;
    private String descripcion;
    @Column(nullable = true)
    private Integer numWebs;

    @Column(nullable = true)
    private Integer numBasesDatos;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecioMensual() {
        return precioMensual;
    }

    public void setPrecioMensual(BigDecimal precioMensual) {
        this.precioMensual = precioMensual;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getNumWebs() {
        return numWebs;
    }

    public void setNumWebs(Integer numWebs) {
        this.numWebs = numWebs;
    }

    public Integer getNumBasesDatos() {
        return numBasesDatos;
    }

    public void setNumBasesDatos(Integer numBasesDatos) {
        this.numBasesDatos = numBasesDatos;
    }
}