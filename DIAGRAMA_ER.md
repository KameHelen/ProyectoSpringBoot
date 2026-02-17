# Diagrama Entidad-Relación (E-R) Normalizado

A continuación se muestra el esquema de la base de datos del sistema, representando las entidades principales y sus relaciones.

```mermaid
erDiagram
    USUARIO ||--|| PERFIL : "tiene un"
    USUARIO ||--o{ SUSCRIPCION : "realiza"
    USUARIO {
        Long id PK
        String email
        String password
        String rol
    }
    PERFIL {
        Long id PK
        String nombre
        String apellido
        String telefono
        String pais
    }

    SUSCRIPCION }o--|| PLAN : "pertenece a"
    SUSCRIPCION }o--|| METODO_PAGO : "usa"
    SUSCRIPCION {
        Long id PK
        Enum estado
        LocalDate fechaInicio
        LocalDate fechaFin
    }

    PLAN {
        Long id PK
        String nombre
        BigDecimal precioMensual
        Integer numWebs
        Integer numBasesDatos
    }

    SUSCRIPCION ||--o{ FACTURA : "genera"
    FACTURA }o--|| TIPO_PAGO : "se paga con"
    FACTURA {
        Long id PK
        BigDecimal cantidad
        LocalDate fechaEmision
        String estado
    }
    
    METODO_PAGO {
        Long id PK
        String tipo
        String detalles
    }
    
    TIPO_PAGO {
        Long id PK
        String nombre
    }
```

## Normalización

El esquema cumple con la Tercera Forma Normal (3NF):
1.  **1NF**: Todos los atributos son atómicos.
2.  **2NF**: No hay dependencias parciales (todas las tablas tienen PK única).
3.  **3NF**: No hay dependencias transitivas (ej. los detalles del usuario están en `Perfil` y no duplicados en `Suscripcion`).

## Auditoría (Envers)

Las entidades `Usuario`, `Plan`, y `Suscripcion` cuentan con tablas de auditoría (`_AUD`) generadas automáticamente por Hibernate Envers para mantener un historial de cambios.
