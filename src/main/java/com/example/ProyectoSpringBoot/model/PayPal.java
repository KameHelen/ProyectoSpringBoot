package com.example.ProyectoSpringBoot.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@org.hibernate.envers.Audited
@DiscriminatorValue("PAYPAL")
public class PayPal extends MetodoPago {

    private String emailPaypal;

    public String getEmailPaypal() {
        return emailPaypal;
    }

    public void setEmailPaypal(String emailPaypal) {
        this.emailPaypal = emailPaypal;
    }
}
