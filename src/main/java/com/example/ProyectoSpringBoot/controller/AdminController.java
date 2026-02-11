package com.example.ProyectoSpringBoot.controller;

import com.example.ProyectoSpringBoot.model.EstadoSuscripcion;
import com.example.ProyectoSpringBoot.model.Suscripcion;
import com.example.ProyectoSpringBoot.model.Usuario;
import com.example.ProyectoSpringBoot.repository.SuscripcionRepository;
import com.example.ProyectoSpringBoot.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @GetMapping("/usuarios")
    public String listUsuarios(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        // We might want to fetch subscriptions eagerly or just rely on lazy loading if
        // transaction is open
        // For simplicity in this demo, we'll just pass users andlet the view access
        // subscriptions if mapped
        // Ideally, we should pass a DTO or specific object, but let's stick to simple
        // first.

        // Better approach: Pass a list of DTOs or a structured object to avoid N+1 in
        // view
        // But for now, let's just pass users and ensure we can access their active
        // subscription

        // To make it easier, let's fetch all subscriptions and put them in a map or
        // just list them
        List<Suscripcion> suscripciones = suscripcionRepository.findAll();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("suscripciones", suscripciones); // We can filter in view or controller
        model.addAttribute("estados", EstadoSuscripcion.values());

        return "admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/estado")
    public String updateEstadoSuscripcion(@PathVariable Long id, @RequestParam EstadoSuscripcion estado) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();

        // Find user's subscription (assuming one active/main subscription for now)
        // In a real world, we might need to specify WHICH subscription if multiple
        // exist
        // For this demo, let's find the most recent or any subscription

        Suscripcion suscripcion = suscripcionRepository.findByUsuario(usuario).stream()
                .findFirst() // Just get one for now
                .orElse(null);

        if (suscripcion != null) {
            suscripcion.setEstado(estado);
            suscripcionRepository.save(suscripcion);
        } else {
            // Handle case where user has no subscription
        }

        return "redirect:/admin/usuarios";
    }

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/usuarios/{id}")
    public String verUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();

        // Audit Reader
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        // Fetch user revisions
        List<Object[]> userRevisions = auditReader.createQuery()
                .forRevisionsOfEntity(Usuario.class, false, true)
                .add(AuditEntity.id().eq(id))
                .getResultList();

        model.addAttribute("usuario", usuario);
        model.addAttribute("revisions", userRevisions);

        // Also fetch subscription if exists
        Suscripcion suscripcion = suscripcionRepository.findByUsuario(usuario).stream().findFirst().orElse(null);
        model.addAttribute("suscripcion", suscripcion);

        return "admin/usuario_detalle";
    }
}
