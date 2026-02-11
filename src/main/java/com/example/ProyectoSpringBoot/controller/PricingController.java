package com.example.ProyectoSpringBoot.controller;

import com.example.ProyectoSpringBoot.model.*;
import com.example.ProyectoSpringBoot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/pricing")
public class PricingController {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String showPricing(Model model) {
        model.addAttribute("planes", planRepository.findAll());
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
