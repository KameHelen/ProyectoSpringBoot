package com.example.ProyectoSpringBoot.controller;

import com.example.ProyectoSpringBoot.model.*;
import com.example.ProyectoSpringBoot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanRepository planRepository;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("perfil", new Perfil());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute Usuario usuario, @ModelAttribute Perfil perfil,
            jakarta.servlet.http.HttpServletRequest request) {
        String password = usuario.getPassword(); // Capture password before save (if encoded later)
        // Check if user exists
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return "redirect:/register?error=exists";
        }

        // Save User & Profile
        usuario.setRol("ROLE_USER");
        usuario.setPerfil(perfil);
        usuarioRepository.save(usuario);

        // LOGIC VERIFICATION: Print Tax to Console
        // We calculate tax on BASIC plan price just for demonstration as requested
        Plan basicPlan = planRepository.findByNombre("BASIC").orElse(null);
        if (basicPlan != null) {
            calculateAndLogTax(perfil.getPais(), basicPlan.getPrecioMensual());
        }

        // AUTO LOGIN
        try {
            // password variable already captured at start of method
            request.login(usuario.getEmail(), password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/pricing";
    }

    private void calculateAndLogTax(String pais, java.math.BigDecimal amount) {
        java.math.BigDecimal taxRate;
        switch (pais) {
            case "ES":
                taxRate = new java.math.BigDecimal("0.21");
                break;
            case "PT":
                taxRate = new java.math.BigDecimal("0.23");
                break;
            case "FR":
                taxRate = new java.math.BigDecimal("0.20");
                break;
            case "US":
                taxRate = new java.math.BigDecimal("0.10");
                break;
            default:
                taxRate = new java.math.BigDecimal("0.15");
                break;
        }

        java.math.BigDecimal taxAmount = amount.multiply(taxRate);
        System.out.println("==================================================");
        System.out.println("VERIFICACIÓN IMPUESTOS (NUEVO REGISTRO)");
        System.out.println("País: " + pais);
        System.out.println("Precio Plan Base (Ref): " + amount + "€");
        System.out.println("Tasa Impuesto: " + (taxRate.multiply(new java.math.BigDecimal("100"))) + "%");
        System.out.println("Impuesto Calculado: " + taxAmount + "€");
        System.out.println("==================================================");
    }
}
