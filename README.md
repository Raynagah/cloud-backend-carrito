# Pedidos360 - Microservicio de Carrito (`ms-carrito`)

Microservicio desacoplado responsable del manejo del estado del carrito de compras activo de cada usuario para la plataforma **Pedidos360**. Se encarga de agregar ítems, sumar cantidades, recalcular subtotales y vaciar la cesta.

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3.2.x
* **Persistencia:** Spring Data JPA / PostgreSQL
* **Seguridad:** OAuth2 Resource Server (Validación JWT con Azure AD)
* **Contenedorización:** Docker

## ⚙️ Instalación y Ejecución

### Requisitos Previos

* JDK 17
* Maven 3.8+
* PostgreSQL (Base de datos `db_carrito`)
* Docker

### Variables de Entorno

| Variable | Valor por Defecto / Descripción |
| :--- | :--- |
| `AZURE_TENANT_ID` | `78b145ef-56b9-4397-b87c-27b242a9fce5` |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<HOST_BD>:5432/db_carrito` |
| `SPRING_DATASOURCE_USERNAME` | Credencial de base de datos |
| `SPRING_DATASOURCE_PASSWORD` | Credencial de base de datos |

### Endpoints Principales

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/v1/carritos/{usuarioId}` | Obtiene el carrito activo del usuario |
| `POST` | `/api/v1/carritos/{usuarioId}/items` | Agrega o suma un producto al carrito |
| `DELETE` | `/api/v1/carritos/{usuarioId}` | Vacía el carrito activo |

### Compilación Local

```bash
mvn clean package -DskipTests
```

### Despliegue con Docker

1. **Construir la imagen:**

```bash
docker build -t pedidos360/ms-carrito:v1 .
```

2. **Ejecutar contenedor:**

```bash
docker run -d \
  --name ms-carrito \
  -p 8083:8083 \
  -e AZURE_TENANT_ID="78b145ef-56b9-4397-b87c-27b242a9fce5" \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://<HOST_BD>:5432/db_carrito" \
  -e SPRING_DATASOURCE_USERNAME="postgres" \
  -e SPRING_DATASOURCE_PASSWORD="password" \
  pedidos360/ms-carrito:v1
```

---

## 🔗 Ecosistema de Repositorios

### Backend

* [Microservicio Carrito (Este repositorio)](https://github.com/Raynagah/cloud-backend-carrito)
* [BFF Orchestrator](https://github.com/Raynagah/cloud-backend-bff)
* [Microservicio Producto](https://github.com/Raynagah/cloud-backend-producto)
* [Microservicio Usuarios](https://github.com/NBello26/ms-usuarios-cloud.git)
* [Microservicio Base de Datos](https://github.com/NBello26/ms-bd-cloud)

### Frontend

* [Frontend React](https://github.com/Raynagah/cloud-frontend.git)
