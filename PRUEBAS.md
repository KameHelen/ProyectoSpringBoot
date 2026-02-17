# Documentación de Pruebas Unitarias del Sistema (SaaS)

| Caso de Prueba | Servicio | Descripción | Resultado Esperado | Estado |
| --- | --- | --- | --- | --- |
| `testCalculateTotalWithTax_Spain` | `TaxService` | Calcula precio total aplicado a España (21%) | 100 + 21% = 121.00 | ✅ Aprobado |
| `testCalculateTotalWithTax_US` | `TaxService` | Calcula precio total aplicado a EEUU (10%) | 100 + 10% = 110.00 | ✅ Aprobado |
| `testCalculateTotalWithTax_Unknown` | `TaxService` | Calcula precio total aplicado a país desconocido (Default 21%) | 100 + 21% = 121.00 | ✅ Aprobado |
| `testCalculateTotalWithTax_NullAmount` | `TaxService` | Calcula precio total con monto nulo | Retorna 0 | ✅ Aprobado |
| `testLoadUserByUsername_Success` | `UsuarioService` | Carga de usuario existente por email | `UserDetails` no nulo, username correcto, rol correcto | ✅ Aprobado |
| `testLoadUserByUsername_NotFound` | `UsuarioService` | Carga de usuario inexistente por email | Lanza `UsernameNotFoundException` | ✅ Aprobado |
| `testRenewSubscriptions` | `SubscriptionService` | Renovación de suscripciones activas que expiran hoy | - Factura generada y guardada (PENDIENTE)<br>- Suscripción actualizada (+1 mes) | ✅ Aprobado |

## Ejecución de Pruebas

Para ejecutar estas pruebas manualmente, utiliza el siguiente comando en la raíz del proyecto:

```bash
./mvnw test
```

Los resultados se mostrarán en la consola indicando `Tests run: X, Failures: 0`.
