package com.example.ProyectoSpringBoot.service;

import com.example.ProyectoSpringBoot.model.*;
import com.example.ProyectoSpringBoot.repository.FacturaRepository;
import com.example.ProyectoSpringBoot.repository.SuscripcionRepository;
import com.example.ProyectoSpringBoot.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriticalServicesTest {

    @Nested
    @DisplayName("Pruebas de TaxService (Impuestos)")
    class TaxServiceTests {

        private TaxService taxService;

        @BeforeEach
        void setUp() {
            taxService = new TaxService();
        }

        @Test
        @DisplayName("Calcular total con impuestos para España (21%)")
        void testCalculateTotalWithTax_Spain() {
            BigDecimal amount = new BigDecimal("100.00");
            BigDecimal result = taxService.calculateTotalWithTax(amount, "ES");
            // 100 * 1.21 = 121.00
            assertEquals(0, new BigDecimal("121.00").compareTo(result));
        }

        @Test
        @DisplayName("Calcular total con impuestos para EEUU (10%)")
        void testCalculateTotalWithTax_US() {
            BigDecimal amount = new BigDecimal("100.00");
            BigDecimal result = taxService.calculateTotalWithTax(amount, "US");
            // 100 * 1.10 = 110.00
            assertEquals(0, new BigDecimal("110.00").compareTo(result));
        }

        @Test
        @DisplayName("Calcular total con impuestos para país desconocido (Defecto 21%)")
        void testCalculateTotalWithTax_Unknown() {
            BigDecimal amount = new BigDecimal("100.00");
            BigDecimal result = taxService.calculateTotalWithTax(amount, "PaisDesconocido");
            // Defecto 21% -> 121.00
            assertEquals(0, new BigDecimal("121.00").compareTo(result));
        }

        @Test
        @DisplayName("Calcular total con monto nulo")
        void testCalculateTotalWithTax_NullAmount() {
            BigDecimal result = taxService.calculateTotalWithTax(null, "ES");
            assertEquals(BigDecimal.ZERO, result);
        }
    }

    @Nested
    @DisplayName("Pruebas de UsuarioService")
    class UsuarioServiceTests {

        @Mock
        private UsuarioRepository usuarioRepository;

        @InjectMocks
        private UsuarioService usuarioService;

        @Test
        @DisplayName("Cargar usuario por nombre de usuario (Email) - Éxito")
        void testLoadUserByUsername_Success() {
            Usuario mockUser = new Usuario();
            mockUser.setEmail("test@example.com");
            mockUser.setPassword("password123");
            mockUser.setRol("ROLE_USER");

            when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

            UserDetails userDetails = usuarioService.loadUserByUsername("test@example.com");

            assertNotNull(userDetails);
            assertEquals("test@example.com", userDetails.getUsername());
            assertEquals("password123", userDetails.getPassword());
            assertTrue(userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        }

        @Test
        @DisplayName("Cargar usuario por nombre de usuario - No Encontrado")
        void testLoadUserByUsername_NotFound() {
            when(usuarioRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () -> {
                usuarioService.loadUserByUsername("noexiste@example.com");
            });
        }
    }

    @Nested
    @DisplayName("Pruebas de SubscriptionService (Renovaciones)")
    class SubscriptionServiceTests {

        @Mock
        private SuscripcionRepository suscripcionRepository;

        @Mock
        private FacturaRepository facturaRepository;

        @Mock
        private TaxService taxService;

        @InjectMocks
        private SubscriptionService subscriptionService;

        @Test
        @DisplayName("Renovación de suscripciones activas - Flujo exitoso")
        void testRenewSubscriptions() {
            // Configurar datos de prueba
            LocalDate today = LocalDate.now();

            Usuario user = new Usuario();
            user.setId(1L);
            Perfil perfil = new Perfil();
            perfil.setPais("ES");
            user.setPerfil(perfil);

            Plan plan = new Plan();
            plan.setPrecioMensual(new BigDecimal("10.00"));

            Suscripcion sub = new Suscripcion();
            sub.setId(100L);
            sub.setUsuario(user);
            sub.setPlan(plan);
            sub.setFechaFin(today); // Expira hoy
            sub.setEstado(EstadoSuscripcion.ACTIVA);

            // Simular respuestas del repositorio
            when(suscripcionRepository.findByEstadoAndFechaFin(EstadoSuscripcion.ACTIVA, today))
                    .thenReturn(Collections.singletonList(sub));

            // Simular servicio de impuestos
            when(taxService.calculateTotalWithTax(any(BigDecimal.class), eq("ES")))
                    .thenReturn(new BigDecimal("12.10"));

            // Ejecutar método a probar
            subscriptionService.renewSubscriptions();

            // Verificar
            // 1. Verificar que la factura se guarda
            verify(facturaRepository, times(1)).save(any(Factura.class));

            // 2. Verificar que la suscripción se actualiza
            // Debe guardarse con nueva fecha de fin (hoy + 1 mes)
            verify(suscripcionRepository, times(1)).save(argThat(savedSub -> {
                return savedSub.getId().equals(100L)
                        && savedSub.getFechaFin().equals(today.plusMonths(1));
            }));
        }
    }
}
