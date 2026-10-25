# Microservicio de Huéspedes (`huespedes-service`)

Este microservicio forma parte del **Sistema de Alquileres de Hotel mediante Microservicios** de la Universidad Nacional de Ucayali (2026), encargado de la administración autónoma del catálogo y ciclo de vida de los datos de huéspedes.

---

## 📋 Requerimientos y Reglas de Negocio Implementadas

- **RF04 (Registro de Huésped):** Permite registrar la información completa del huésped: nombres, apellidos, DNI único, teléfono, email, dirección, nacionalidad, fecha de nacimiento, fechas de ingreso y salida, y estado lógico.
- **RF05 (Consulta y Listado):** Consulta individual por ID, por DNI (búsqueda rápida para recepción) y listado global (con filtro opcional de solo activos).
- **RF06 (Validación Estricta):** Validación rigurosa con Bean Validation (`@Valid`):
  - DNI: Exactamente 8 dígitos numéricos y restricción de unicidad (`UQ_huespedes_dni`).
  - Nombres y Apellidos: Solo caracteres alfabéticos y espacios (incluye acentos y diéresis).
  - Correo electrónico: Formato RFC válido.
  - Teléfono: 9 dígitos numéricos.
  - Fechas: Fecha de nacimiento en el pasado (`@Past`) y fecha de salida posterior a la de ingreso.
- **RN04 (Integridad en Eliminación Lógica):** No se permite eliminar físicamente ni dar de baja lógica (`estado_logico = false`) a un huésped que mantenga un alquiler vigente o activo. Si tiene un alquiler activo, la API responde con **`409 Conflict`**.

---

## 🏗️ Arquitectura por Capas

El proyecto sigue una estructura limpia (Clean Architecture / Domain-Driven Design adaptado):

```
com.hotel.huespedes/
├── HuespedesServiceApplication.java       # Clase principal Spring Boot
├── client/                                # Clientes HTTP síncronos inter-servicio
│   ├── AlquileresClient.java              # Validación RN04 contra alquileres-service
│   └── dto/AlquilerActivoResponse.java
├── config/                                # Configuraciones de OpenAPI y beans
│   └── OpenApiConfig.java
├── controller/                            # Controladores REST
│   └── HuespedController.java
├── dto/                                   # Objetos de Transferencia de Datos
│   ├── request/
│   │   ├── HuespedCreateRequest.java
│   │   └── HuespedUpdateRequest.java
│   └── response/
│       ├── ApiResponse.java
│       ├── ErrorResponse.java
│       └── HuespedResponse.java
├── entity/                                # Entidades JPA (db_huespedes)
│   └── Huesped.java
├── exception/                             # Manejo global de excepciones
│   ├── BusinessRuleException.java        # HTTP 409
│   ├── DuplicateResourceException.java    # HTTP 409
│   ├── ResourceNotFoundException.java     # HTTP 404
│   └── GlobalExceptionHandler.java        # Estandarización de errores y Bean Validation
├── repository/                            # Repositorios Spring Data JPA
│   └── HuespedRepository.java
└── service/                               # Lógica de negocio (Contrato e Implementación)
    ├── HuespedService.java
    └── impl/HuespedServiceImpl.java
```

---

## 🗄️ Base de Datos SQL Server (`db_huespedes`)

El microservicio utiliza **Microsoft SQL Server**. El script DDL oficial se ubica en:
`src/main/resources/db/script_creacion_db_huespedes.sql`

```sql
CREATE DATABASE db_huespedes;
GO

USE db_huespedes;
GO

CREATE TABLE huespedes (
    id INT IDENTITY(1,1) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(8) NOT NULL,
    tel VARCHAR(9) NULL,
    email VARCHAR(150) NULL,
    direccion VARCHAR(200) NULL,
    nacionalidad VARCHAR(50) NULL,
    fecha_nacimiento DATE NULL,
    fecha_ingreso DATE NULL,
    fecha_salida DATE NULL,
    estado_logico BIT NOT NULL DEFAULT 1,
    fecha_registro DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT PK_huespedes PRIMARY KEY CLUSTERED (id ASC),
    CONSTRAINT UQ_huespedes_dni UNIQUE (dni)
);

CREATE NONCLUSTERED INDEX IX_huespedes_dni ON huespedes(dni);
CREATE NONCLUSTERED INDEX IX_huespedes_estado_logico ON huespedes(estado_logico);
```

---

## 🚀 Endpoints REST de la API

| Método | Endpoint | Descripción | Código Éxito |
|---|---|---|---|
| `POST` | `/api/huespedes` | Registra un nuevo huésped (RF04, RF06) | `201 Created` |
| `GET` | `/api/huespedes` | Lista huéspedes (`?soloActivos=true/false`) (RF05) | `200 OK` |
| `GET` | `/api/huespedes/{id}` | Consulta de huésped por ID (RF05) | `200 OK` |
| `GET` | `/api/huespedes/dni/{dni}` | Consulta rápida de huésped por DNI | `200 OK` |
| `GET` | `/api/huespedes/{id}/existe` | Valida existencia y estado activo para `alquileres-service` | `200 OK` |
| `PUT` | `/api/huespedes/{id}` | Actualiza datos del huésped | `200 OK` |
| `DELETE` | `/api/huespedes/{id}` | Baja lógica del huésped (RN04) | `200 OK` |

---

## 📖 Documentación Interactiva Swagger / OpenAPI

Al iniciar el microservicio en el puerto `8082`, la documentación Swagger se encuentra disponible en:
- **Swagger UI:** `http://localhost:8082/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8082/v3/api-docs`
