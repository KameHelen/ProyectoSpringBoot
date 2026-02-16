package com.example.ProyectoSpringBoot.controller;

import com.example.ProyectoSpringBoot.model.*;
import com.example.ProyectoSpringBoot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import com.example.ProyectoSpringBoot.dto.PlanWithTaxDTO;
import com.example.ProyectoSpringBoot.service.TaxService;

@Controller
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private TaxService taxService;

    @GetMapping
    public String listFacturas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            Model model,
            Authentication authentication) {

        // User Logic
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        String country = "Spain"; // Default

        if (usuario != null) {
            String userName = usuario.getEmail(); // Default fallback
            if (usuario.getPerfil() != null) {
                if (usuario.getPerfil().getPais() != null) {
                    country = usuario.getPerfil().getPais();
                }
                if (usuario.getPerfil().getNombre() != null) {
                    userName = usuario.getPerfil().getNombre();
                    if (usuario.getPerfil().getApellido() != null) {
                        userName += " " + usuario.getPerfil().getApellido();
                    }
                }
            }
            model.addAttribute("userName", userName);
            model.addAttribute("userEmail", usuario.getEmail());

            Suscripcion suscripcion = suscripcionRepository.findByUsuarioAndEstado(usuario, EstadoSuscripcion.ACTIVA)
                    .orElse(null);
            model.addAttribute("suscripcion", suscripcion);

            if (suscripcion != null) {
                BigDecimal taxAmount = taxService.calculateTax(suscripcion.getPlan().getPrecioMensual(), country);
                BigDecimal totalWithTax = taxService.calculateTotalWithTax(suscripcion.getPlan().getPrecioMensual(),
                        country);
                model.addAttribute("currentTaxAmount", taxAmount);
                model.addAttribute("currentTotal", totalWithTax);
            }

            String finalCountry = country;
            List<PlanWithTaxDTO> planesDTO = planRepository.findAll().stream().map(p -> {
                BigDecimal taxRate = taxService.getTaxRate(finalCountry);
                BigDecimal tAmount = taxService.calculateTax(p.getPrecioMensual(), finalCountry);
                BigDecimal total = taxService.calculateTotalWithTax(p.getPrecioMensual(), finalCountry);
                return new PlanWithTaxDTO(p, taxRate, tAmount, total);
            }).collect(Collectors.toList());

            model.addAttribute("planes", planesDTO);
            model.addAttribute("userCountry", country);
        }

        List<Factura> facturas;

        if (startDate != null && endDate != null) {
            facturas = facturaRepository.findByFechaEmisionBetween(startDate, endDate);
        } else if (minAmount != null) {
            facturas = facturaRepository.findByCantidadGreaterThanEqual(minAmount);
        } else {
            // For real app, filtering by user is needed. Demo: showing all or filtered by
            // dates/amount.
            // If we want to show only user's invoices:
            facturas = facturaRepository.findAll();
        }

        model.addAttribute("facturas", facturas);
        return "facturas"; // view name
    }

    @PostMapping("/cambiar-plan")
    @jakarta.transaction.Transactional
    public String cambiarPlan(@RequestParam Long planId, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        Suscripcion suscripcion = suscripcionRepository.findByUsuarioAndEstado(usuario, EstadoSuscripcion.ACTIVA)
                .orElseThrow(() -> new RuntimeException("No active subscription"));

        Plan nuevoPlan = planRepository.findById(planId).orElseThrow();

        suscripcion.setPlan(nuevoPlan);
        suscripcionRepository.save(suscripcion);

        return "redirect:/facturas";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String newPassword, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        // In a real app, use PasswordEncoder! For this demo (NoOp), just set it.
        usuario.setPassword(newPassword);
        usuarioRepository.save(usuario);
        return "redirect:/facturas?passwordChanged";
    }

    @PostMapping("/pagar")
    @jakarta.transaction.Transactional
    public String pagarMensualidad(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        Suscripcion suscripcion = suscripcionRepository.findByUsuarioAndEstado(usuario, EstadoSuscripcion.ACTIVA)
                .orElse(null);

        if (suscripcion != null && suscripcion.getFechaFin() != null) {
            LocalDate today = LocalDate.now();

            // Check if already paid (e.g., fechaFin is more than 3 days in the future)
            // Just a simple rule: if date is in future, consider it paid for current cycle.
            if (suscripcion.getFechaFin().isAfter(today)) {
                long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, suscripcion.getFechaFin());
                return "redirect:/facturas?alreadyPaid&days=" + daysRemaining;
            }

            // Process Payment (Extend by 30 days)
            // If expired, start from today + 30 days. If active but due, add 30 days to
            // current end date.
            LocalDate newEndDate;
            if (suscripcion.getFechaFin().isBefore(today)) {
                newEndDate = today.plusDays(30);
            } else {
                newEndDate = suscripcion.getFechaFin().plusDays(30);
            }
            suscripcion.setFechaFin(newEndDate);
            suscripcionRepository.save(suscripcion);

            return "redirect:/facturas?paymentSuccess";
        }

        return "redirect:/facturas?error";
    }
}
