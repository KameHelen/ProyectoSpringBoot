package com.example.ProyectoSpringBoot.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PAYPAL")
public class PagoPayPal extends TipoPago {
    private String correoPaypal;
    // getters y setters

    public String getCorreoPaypal() {
        return correoPaypal;
    }

    public void setCorreoPaypal(String correoPaypal) {
        this.correoPaypal = correoPaypal;
    }
}
