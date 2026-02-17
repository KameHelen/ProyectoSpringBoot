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
    public String listUsuarios(@RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Search Filter (Email)
        if (search != null && !search.isEmpty()) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getEmail().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }

        // Status Filter
        if (status != null && !status.isEmpty()) {
            java.time.LocalDate today = java.time.LocalDate.now();
            usuarios = usuarios.stream().filter(u -> {
                Suscripcion sub = u.getSuscripciones() != null && !u.getSuscripciones().isEmpty()
                        ? u.getSuscripciones().get(0)
                        : null;
                boolean match = false;

                if ("MOROSO".equals(status)) {
                    match = sub != null && sub.getEstado() == EstadoSuscripcion.MOROSA;
                } else if ("PAGADO".equals(status)) {
                    match = sub != null && sub.getEstado() != EstadoSuscripcion.MOROSA && sub.getFechaFin() != null
                            && !sub.getFechaFin().isBefore(today);
                } else if ("NO_PAGADO".equals(status)) {
                    match = sub == null || (sub.getEstado() != EstadoSuscripcion.MOROSA
                            && (sub.getFechaFin() == null || sub.getFechaFin().isBefore(today)));
                }
                return match;
            }).toList();
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("estados", EstadoSuscripcion.values());
        model.addAttribute("search", search);
        model.addAttribute("status", status);

        return "admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/delete")
    public String deleteUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        Suscripcion sub = usuario.getSuscripciones() != null && !usuario.getSuscripciones().isEmpty()
                ? usuario.getSuscripciones().get(0)
                : null;

        if (sub != null && sub.getEstado() == EstadoSuscripcion.MOROSA) {
            usuarioRepository.delete(usuario);
        }

        return "redirect:/admin/usuarios";
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
        List<Object[]> userRevisions = new java.util.ArrayList<>();
        try {
            userRevisions = auditReader.createQuery()
                    .forRevisionsOfEntity(Usuario.class, false, true)
                    .add(AuditEntity.id().eq(id))
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Error fetching user revisions for user " + id + ": " + e.getMessage());
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("revisions", userRevisions);

        // Also fetch subscription if exists
        List<Suscripcion> suscripciones = suscripcionRepository.findByUsuario(usuario);
        Suscripcion suscripcion = suscripciones.stream().findFirst().orElse(null);
        model.addAttribute("suscripcion", suscripcion);

        // Fetch subscription revisions (Movimientos)
        List<Object[]> rawRevisions = new java.util.ArrayList<>();
        try {
            rawRevisions = auditReader.createQuery()
                    .forRevisionsOfEntity(Suscripcion.class, false, true)
                    .add(AuditEntity.relatedId("usuario").eq(id))
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Error fetching subscription revisions for user " + id + ": " + e.getMessage());
        }

        // Process revisions to handle broken foreign keys (e.g. deleted Plans) safely
        List<java.util.Map<String, Object>> processedRevisions = new java.util.ArrayList<>();
        for (Object[] rev : rawRevisions) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            Suscripcion sub = (Suscripcion) rev[0];
            Object revEntity = rev[1];
            Object revType = rev[2];

            // Timestamp
            long timestamp = 0;
            if (revEntity instanceof org.hibernate.envers.DefaultRevisionEntity) {
                timestamp = ((org.hibernate.envers.DefaultRevisionEntity) revEntity).getTimestamp();
            }
            map.put("timestamp", new java.util.Date(timestamp));

            // Revision Type
            map.put("tipo", revType);

            // Safe Plan Name Access
            String planNombre = "-";
            try {
                if (sub.getPlan() != null) {
                    planNombre = sub.getPlan().getNombre();
                }
            } catch (Exception e) {
                // Determine if it's EntityNotFoundException or similar
                planNombre = "Plan Eliminado";
            }
            map.put("planNombre", planNombre);

            map.put("estado", sub.getEstado());
            map.put("fechaFin", sub.getFechaFin());

            processedRevisions.add(map);
        }

        // Sort by timestamp descending (newest first)
        processedRevisions.sort((r1, r2) -> {
            java.util.Date d1 = (java.util.Date) r1.get("timestamp");
            java.util.Date d2 = (java.util.Date) r2.get("timestamp");
            return d2.compareTo(d1);
        });

        model.addAttribute("suscripcionRevisions", processedRevisions);

        return "admin/usuario_detalle";
    }
}
