package com.example.ProyectoSpringBoot.controller;

import com.example.ProyectoSpringBoot.dto.PlanWithTaxDTO;
import com.example.ProyectoSpringBoot.service.TaxService;
import com.example.ProyectoSpringBoot.model.*;
import com.example.ProyectoSpringBoot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pricing")
public class PricingController {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TaxService taxService;

    @GetMapping
    public String showPricing(Model model, Authentication authentication) {
        String country = "Spain"; // Default
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
            if (usuario != null && usuario.getPerfil() != null && usuario.getPerfil().getPais() != null) {
                country = usuario.getPerfil().getPais();
            }
        }

        String finalCountry = country;
        List<PlanWithTaxDTO> planesConImpuestos = planRepository.findAll().stream().map(plan -> {
            BigDecimal taxRate = taxService.getTaxRate(finalCountry);
            BigDecimal taxAmount = taxService.calculateTax(plan.getPrecioMensual(), finalCountry);
            BigDecimal totalPrice = taxService.calculateTotalWithTax(plan.getPrecioMensual(), finalCountry);
            return new PlanWithTaxDTO(plan, taxRate, taxAmount, totalPrice);
        }).collect(Collectors.toList());

        model.addAttribute("planes", planesConImpuestos);
        return "pricing";
    }

    @PostMapping("/select")
    public String selectPlan(@RequestParam Long planId, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        Plan plan = planRepository.findById(planId).orElseThrow();

        Suscripcion sub = new Suscripcion();
        sub.setUsuario(usuario);
        sub.setPlan(plan);
        sub.setEstado(EstadoSuscripcion.ACTIVA);
        sub.setFechaInicio(LocalDate.now());
        sub.setFechaFin(LocalDate.now().plusMonths(1));

        suscripcionRepository.save(sub);

        return "redirect:/facturas";
    }
}
