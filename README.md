# Sistema de Gestión de Suscripciones (SaaS)

Este proyecto es una plataforma SaaS completa desarrollada con Spring Boot para la gestión de suscripciones, usuarios, facturación e impuestos. Incluye un panel de administración, auditoría de datos y un sistema de temas visuales premium.

## 🚀 Tecnologías Utilizadas

*   **Backend**: Java 21, Spring Boot 3.4.2
*   **Base de Datos**: PostgreSQL (Producción/Test), H2 (Memoria posible)
*   **ORM**: Hibernate / Spring Data JPA
*   **Seguridad**: Spring Security 6
*   **Frontend**: Thymeleaf, HTML5, CSS3 (Diseño Premium Dark/Light)
*   **Auditoría**: Hibernate Envers
*   **Pruebas**: JUnit 5, Mockito

## ⚙️ Configuración y Ejecución

### Requisitos Previos
*   Java JDK 21 instalado
*   Maven instalado
*   PostgreSQL corriendo (Base de datos `saas_platform`, user `postgres`, pass `admin123` según `application.properties`)

### Pasos para ejecutar
1.  Clonar el repositorio.
2.  Configurar la base de datos en `src/main/resources/application.properties` si es necesario.
3.  Ejecutar el comando:
    ```bash
    ./mvnw spring-boot:run
    ```
4.  Acceder a la aplicación en `http://localhost:8080`.

## 🧪 Pruebas Unitarias

El sistema cuenta con una suite de pruebas críticas para asegurar la integridad de los cobros y la gestión de usuarios.

Para ejecutar las pruebas:
```bash
./mvnw test
```

## 🌟 Características Principales

*   **Gestión de Usuarios**: Registro, Login, Perfiles con localización (País) para impuestos.
*   **Suscripciones**: Planes dinámicos, renovación automática diaria, cálculo de impuestos basado en el país.
*   **Facturación**: Generación automática de facturas en estado PENDIENTE.
*   **Panel de Administración**: Vista de auditoría, gestión de usuarios.
*   **Diseño UI/UX**: Interfaz moderna con modo oscuro y feedback visual.

## 📁 Estructura del Proyecto

*   `src/main/java`: Código fuente Java.
    *   `controller`: Controladores MVC.
    *   `model`: Entidades JPA.
    *   `repository`: Repositorios de datos.
    *   `service`: Lógica de negocio (Impuestos, Suscripciones).
*   `src/main/resources`: Configuración y vistas.
    *   `templates`: Plantillas Thymeleaf.
    *   `static`: Archivos CSS/JS.
*   `src/test`: Pruebas unitarias Junit.
