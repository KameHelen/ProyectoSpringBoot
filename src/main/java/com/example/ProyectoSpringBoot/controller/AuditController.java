package com.example.ProyectoSpringBoot.controller;

import com.example.ProyectoSpringBoot.model.Usuario;
import com.example.ProyectoSpringBoot.model.Suscripcion;
import jakarta.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/auditoria")
public class AuditController {

    @Autowired
    private EntityManager entityManager;

    @GetMapping
    @SuppressWarnings("unchecked")
    @jakarta.transaction.Transactional
    public String viewAuditLog(Model model) {
        AuditReader reader = AuditReaderFactory.get(entityManager);

        List<Object[]> usuarioRevisions = List.of();
        List<Object[]> suscripcionRevisions = List.of();

        try {
            // Fetch revisions for Usuario
            usuarioRevisions = reader.createQuery()
                    .forRevisionsOfEntity(Usuario.class, false, true)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Error fetching Usuario revisions: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            // Fetch revisions for Suscripcion
            suscripcionRevisions = reader.createQuery()
                    .forRevisionsOfEntity(Suscripcion.class, false, true)
                    .getResultList();

            // Initialize proxies
            for (Object[] rev : suscripcionRevisions) {
                try {
                    Suscripcion sub = (Suscripcion) rev[0];
                    if (sub.getPlan() != null) {
                        sub.getPlan().getNombre(); // Touch
                    }
                    if (sub.getUsuario() != null) {
                        sub.getUsuario().getEmail(); // Touch
                    }
                } catch (Exception ex) {
                    System.err.println("Error immersing proxy: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching Suscripcion revisions: " + e.getMessage());
            e.printStackTrace();
        }

        model.addAttribute("usuarioRevisions", usuarioRevisions);
        model.addAttribute("suscripcionRevisions", suscripcionRevisions);

        return "admin/auditoria";
    }
}
