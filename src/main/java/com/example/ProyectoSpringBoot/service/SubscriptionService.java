package com.example.ProyectoSpringBoot.service;

import com.example.ProyectoSpringBoot.model.*;
import com.example.ProyectoSpringBoot.repository.FacturaRepository;
import com.example.ProyectoSpringBoot.repository.SuscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private TaxService taxService;

    @Scheduled(cron = "0 0 0 * * ?") // Se ejecuta diariamente a medianoche
    @Transactional
    public void renewSubscriptions() {
        LocalDate today = LocalDate.now();
        List<Suscripcion> expiringSubscriptions = suscripcionRepository
                .findByEstadoAndFechaFin(EstadoSuscripcion.ACTIVA, today);

        for (Suscripcion sub : expiringSubscriptions) {
            renewSubscription(sub);
        }
    }

    private void renewSubscription(Suscripcion sub) {
        // Calcular monto
        BigDecimal planPrice = sub.getPlan().getPrecioMensual();
        String country = "ES"; // Por defecto España
        if (sub.getUsuario() != null && sub.getUsuario().getPerfil() != null
                && sub.getUsuario().getPerfil().getPais() != null) {
            country = sub.getUsuario().getPerfil().getPais();
        }

        BigDecimal totalAmount = taxService.calculateTotalWithTax(planPrice, country);

        // Crear Factura
        Factura factura = new Factura();
        factura.setSuscripcion(sub);
        factura.setFechaEmision(LocalDate.now());
        factura.setCantidad(totalAmount);
        factura.setEstado("PENDIENTE"); // Asumo pendiente hasta que se procese el pago real
        // factura.setTipoPago(...) // Debería tomar el último método de pago o default.
        // Por ahora null o manejarlo.

        facturaRepository.save(factura);

        // Extender Suscripción
        sub.setFechaFin(sub.getFechaFin().plusMonths(1));
        suscripcionRepository.save(sub);

        System.out.println("Renovada suscripción " + sub.getId() + " para el usuario " + sub.getUsuario().getId());
    }
}
