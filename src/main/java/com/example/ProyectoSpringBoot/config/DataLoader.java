package com.example.ProyectoSpringBoot.config;

import com.example.ProyectoSpringBoot.model.*;
import com.example.ProyectoSpringBoot.repository.PlanRepository;
import com.example.ProyectoSpringBoot.repository.UsuarioRepository;
import com.example.ProyectoSpringBoot.repository.SuscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataLoader {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            // Create or Update Plans
            createOrUpdatePlan("BASIC", new BigDecimal("9.99"), "Plan básico para empezar.", 1, 5);
            createOrUpdatePlan("PREMIUM", new BigDecimal("19.99"), "Plan avanzado con más recursos.", 5, 20);
            createOrUpdatePlan("ENTERPRISE", new BigDecimal("49.99"), "Plan empresarial ilimitado.", 50, 100);

            // Create or Update Admin
            createOrUpdateUser("admin@example.com", "admin123", "ROLE_ADMIN", "Admin", "User", "US");

            // Create or Update User
            Usuario user = createOrUpdateUser("user@example.com", "user123", "ROLE_USER", "John", "Doe", "ES");

            // Assign subscription to user if not exists
            if (suscripcionRepository.findByUsuarioAndEstado(user, EstadoSuscripcion.ACTIVA).isEmpty()) {
                Plan plan = planRepository.findByNombre("PREMIUM").orElse(null);
                if (plan != null) {
                    Suscripcion sub = new Suscripcion();
                    sub.setUsuario(user);
                    sub.setPlan(plan);
                    sub.setEstado(EstadoSuscripcion.ACTIVA);
                    sub.setFechaInicio(LocalDate.now());
                    sub.setFechaFin(LocalDate.now().plusMonths(1));
                    suscripcionRepository.save(sub);
                }
            }
        };
    }

    private void createOrUpdatePlan(String nombre, BigDecimal precio, String descripcion, int web, int db) {
        Plan plan = planRepository.findByNombre(nombre).orElse(new Plan());
        plan.setNombre(nombre);
        plan.setPrecioMensual(precio);
        plan.setDescripcion(descripcion);
        plan.setNumWebs(web);
        plan.setNumBasesDatos(db);
        planRepository.save(plan);
    }

    private Usuario createOrUpdateUser(String email, String password, String role, String nombre, String apellido,
            String pais) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(new Usuario());

        usuario.setEmail(email);
        usuario.setPassword(password); // Plain text as per configuration
        usuario.setRol(role);

        Perfil perfil = usuario.getPerfil();
        if (perfil == null) {
            perfil = new Perfil();
            usuario.setPerfil(perfil);
        }

        perfil.setNombre(nombre);
        perfil.setApellido(apellido);
        perfil.setPais(pais);

        return usuarioRepository.save(usuario);
    }
}
