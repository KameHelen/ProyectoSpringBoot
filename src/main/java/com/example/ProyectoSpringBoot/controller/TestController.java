package com.example.ProyectoSpringBoot.controller;

import com.example.ProyectoSpringBoot.model.*;
import com.example.ProyectoSpringBoot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // <-- Asegúrate de tener este import
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/api/test")
public class TestController {

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private PlanRepository planRepository;

        @Autowired
        private SuscripcionRepository suscripcionRepository;

        @Autowired
        private FacturaRepository facturaRepository;

        @Autowired
        private PerfilRepository perfilRepository;

        @GetMapping("/formulario-test")
        public String mostrarFormulario(Model model) {
                return "formulario-test"; // Retorna el nombre del archivo HTML (sin .html)
        }

        @PostMapping("/crear-usuario-suscripcion")
        @ResponseBody
        public ResponseEntity<String> crearUsuarioYSuscripcion(
                        @RequestParam String email,
                        @RequestParam String nombre,
                        @RequestParam String apellido,
                        @RequestParam String planNombre) {
                // Crear perfil
                Perfil perfil = new Perfil();
                perfil.setNombre(nombre);
                perfil.setApellido(apellido);
                perfil = perfilRepository.save(perfil);

                // Crear usuario
                Usuario usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setPassword("123456");
                usuario.setPerfil(perfil);
                usuario = usuarioRepository.save(usuario);

                // Buscar plan
                Plan plan = planRepository.findByNombre(planNombre)
                                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

                // Crear suscripción
                Suscripcion suscripcion = new Suscripcion();
                suscripcion.setUsuario(usuario);
                suscripcion.setPlan(plan);
                suscripcion.setEstado(EstadoSuscripcion.ACTIVA);
                suscripcion.setFechaInicio(LocalDate.now());
                suscripcion.setFechaFin(LocalDate.now().plusDays(30));
                suscripcion = suscripcionRepository.save(suscripcion);

                // Crear factura
                Factura factura = new Factura();
                factura.setSuscripcion(suscripcion);
                factura.setCantidad(plan.getPrecioMensual());
                factura.setFechaEmision(LocalDate.now());
                factura.setEstado("PENDIENTE");
                factura = facturaRepository.save(factura);

                // Mensaje personalizado
                String mensaje = String.format(
                                "¡Usuario %s %s registrado correctamente!\n" +
                                                "Suscripción activa al plan: %s.\n" +
                                                "Factura generada con éxito.",
                                nombre, apellido, planNombre);

                return ResponseEntity.ok(mensaje);
        }
}