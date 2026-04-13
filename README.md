# 🏦 Banking Microservices

Sistema bancario distribuido basado en microservicios, construido con **Spring Boot**, **Spring Cloud Gateway**, **Kafka**, **JWT** y **Docker**.

---

## 📋 Requisitos previos

Asegúrate de tener instalado:

- Docker Desktop
- Git
- (Opcional) Java 21 y Maven para desarrollo local

---

## 🚀 Tecnologías

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 21 | Backend |
| Spring Boot | 4.0.3 | Framework principal |
| Spring Cloud Gateway | 4.0.3 | API Gateway + Seguridad |
| PostgreSQL | 15 | Base de datos |
| Apache Kafka | 7.5.0 | Mensajería asíncrona |
| Redis | 7 | Caché |
| Docker | Latest | Contenerización |

---

## 📦 Servicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| API Gateway | 8080 | Punto de entrada único + autenticación JWT |
| Account Service | 8081 | Gestión de clientes y cuentas |
| Transaction Service | 8082 | Transferencias, idempotencia y reversos |
| Notification Service | 8084 | Notificaciones vía Kafka |

---

## 🏗️ Arquitectura
```text
                    ┌───────────────────────┐
                    │     API Gateway       │
                    │   (JWT + Security)    │
                    └──────────┬────────────┘
                               │
        ┌──────────────────────┼────────────────────────┐
        │                      │                        │
        ▼                      ▼                        ▼
┌───────────────┐    ┌───────────────┐       ┌───────────────┐
│ Account       │    │ Transaction   │       │ Notification  │
│ Service       │    │ Service       │       │ Service       │
│ (8081)        │    │ (8082)        │       │ (8084)        │
└───────┬───────┘    └───────┬───────┘       └───────┬───────┘
        │                    │                       │
        └──────────────┬─────┴───────────────┬───────┘
                       ▼                     ▼
                ┌───────────────┐     ┌───────────────┐
                │ PostgreSQL    │     │ Kafka         │
                │ (Datos)       │     │ (Eventos)     │
                └───────────────┘     └───────────────┘
```

## 🐳 Ejecución con Docker

```bash
# Clonar repositorio
git clone https://github.com/Larry-Baltodano/banking-microservices.git
cd banking-microservices

# Dar permisos y ejecutar
chmod +x build-all.sh
./build-all.sh
```

## 📌 Esto levantará automáticamente:

- PostgreSQL
- Kafka + Zookeeper
- Redis
- Todos los microservicios

## 🔐 Credenciales de prueba
| Usuario | Password | Rol |
|------------|---------|-----|
| larry | password | USER |
| admin | admin123 | ADMIN |
| user | user123 | User |

## 📚 Documentación interactiva (Swagger)
### Una vez ejecutado el sistema, accede a:

- API Gateway: http://localhost:8080/swagger-ui.html
- Account Service: http://localhost:8081/swagger-ui.html
- Transaction Service: http://localhost:8082/swagger-ui.html
- Notification Service: http://localhost:8084/swagger-ui.html

### 📊 Endpoints principales

#### Autenticación
`POST http://localhost:8080/auth/login`

**Headers:**

| Key | Value |
| :--- | :--- |
| `Content-Type` | `application/json` |

**Body (JSON):**
```json
{
  "username": "larry",
  "password": "password"
}
```
**Respuesta exitosa (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsYXJyeSIsInJvbGVzIjpbIlVTRVIiXSwiaWF0IjoxNzQ0NTUwNDAwLCJleHAiOjE3NDQ1NTQwMDB9.abc123...",
  "username": "larry",
  "roles": ["USER"],
  "expiresIn": 3600000
}
```
> **Nota:** IMPORTANTE: Para realizar pruebas en Postman, es necesario incluir el Bearer Token obtenido tras el login en el Gateway. Recuerda que el API Gateway actúa como capa de seguridad principal para todos los endpoints.

### Clientes
```
GET    /api/clientes              - Listar todos los clientes
GET    /api/clientes/{id}         - Obtener cliente por ID
POST   /api/clientes              - Crear nuevo cliente
PUT    /api/clientes/{id}         - Actualizar cliente
DELETE /api/clientes/{id}         - Eliminar cliente
```
### Cuentas
```
GET    /api/cuentas/{id}          - Obtener cuenta
GET    /api/cuentas/cliente/{id}  - Listar cuentas de un cliente
GET    /api/cuentas/{id}/saldo    - Consultar saldo
POST   /api/cuentas               - Crear nueva cuenta
```
### Transferencia
```
POST  /api/transacciones/transferencias - Realizar transferencia
GET   /api/transacciones/{id}           - Consultar estado
POST  /api/transacciones/{id}/reversar  - Reversar transferencia
```

### 🛠️ Desarrollo local (sin Docker)
```
# Ejecutar cada servicio individualmente
cd services/account-service
./mvnw spring-boot:run

# En otra terminal
cd services/transaction-service
./mvnw spring-boot:run

# En otra terminal
cd services/notification-service
./mvnw spring-boot:run

# En otra terminal
cd services/api-gateway
./mvnw spring-boot:run
```
### 📁 Estructura del proyecto
```
banking-microservices/
├── services/
│   ├── account-service/
│   ├── transaction-service/
│   ├── notification-service/
│   └── api-gateway/
├── docker-compose.yml
├── build-all.sh
├── init.sql
└── README.md
```
## ✅ Características implementadas
- Microservicios con Spring Boot
- API Gateway con JWT
- Seguridad basada en roles (USER/ADMIN)
- Transferencias bancarias con idempotencia
- Eventos asíncronos con Kafka
- Notificaciones por email/SMS
- Circuit Breaker (Resilience4j)
- Documentación Swagger/OpenAPI
- Docker y Docker Compose

### 📄 Licencia
#### MIT

## 👤 Autor
Larry Baltodano - baltolarry.23@gmail
