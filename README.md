# MTD API

Sistema de gestión de voluntarios desarrollado con Spring Boot que proporciona una API REST completa para la administración de usuarios, archivos y autenticación JWT.

## 👥 Equipo de Trabajo del Curso (IaC)

- Cisneros Bartra, Adrián
- Marin Yupanqui, Bryan
- Márquez Diestra, Hugo
- Mostacero Cieza, Luis

## 🚀 Características

- **Autenticación JWT** - Sistema seguro de tokens para autenticación
- **Gestión de usuarios** - CRUD completo con roles y permisos
- **Almacenamiento en la nube** - Integración con Cloudflare R2 para archivos
- **Base de datos distribuida** - Sincronización con Cloudflare D1
- **Documentación automática** - Swagger/OpenAPI integrado
- **Contenedorización** - Docker y Docker Compose listos para producción
- **CI/CD** - Pipeline automatizado con Jenkins

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 17 | Lenguaje de programación |
| Spring Boot | 3.4.2 | Framework principal |
| Spring Security | 3.4.2 | Seguridad y autenticación |
| MySQL | 8.0.36 | Base de datos principal |
| JWT | 0.11.5 | Tokens de autenticación |
| Docker | Latest | Contenedorización |
| Maven | 3.9+ | Gestión de dependencias |

## 📋 Requisitos

- **Java JDK 17** o superior
- **Maven 3.9+** (incluido Maven Wrapper)
- **Docker** y **Docker Compose**
- **Git**

## ⚡ Inicio Rápido

### 1. Clonar el repositorio
```bash
git clone https://github.com/Sauro-Dev/MTD-APP-BACK.git
cd MTD-APP-BACK
```

### 2. Configurar variables de entorno
Crear archivo `.env`:
```bash
# Base de datos
MYSQL_ROOT_PASSWORD=tu_password_seguro
MYSQL_DATABASE=mtd
MYSQL_USER=mtd_user
MYSQL_PASSWORD=tu_password_usuario

# Cloudflare R2 (Almacenamiento)
CLOUDFLARE_R2_ACCESS_KEY=tu_access_key
CLOUDFLARE_R2_SECRET_KEY=tu_secret_key
CLOUDFLARE_R2_ENDPOINT=tu_endpoint
CLOUDFLARE_R2_BUCKET_NAME=tu_bucket

# Cloudflare D1 (Base de datos distribuida)
CLOUDFLARE_D1_DATABASE_ID=tu_database_id
CLOUDFLARE_D1_API_TOKEN=tu_api_token

# Correo electrónico
SPRING_MAIL_USERNAME=tu_email@gmail.com
SPRING_MAIL_PASSWORD=tu_app_password
```

### 3. Ejecutar con Docker (Recomendado)
```bash
docker-compose up -d
```

### 4. Ejecutar en desarrollo
```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 📚 Documentación API

Una vez iniciada la aplicación:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## 🔐 Endpoints Principales

### Autenticación
```http
POST /api/v1/users/login
POST /api/v1/users/register
GET  /api/v1/users/profile
PUT  /api/v1/users/profile
```

### Gestión de Archivos
```http
POST /api/v1/landing-files/upload
GET  /api/v1/landing-files/all
GET  /api/v1/landing-files/download/{id}
DELETE /api/v1/landing-files/{id}
```

### Administración
```http
GET /api/v1/users
GET /api/v1/users/{id}
```

## 🏗️ Arquitectura

```
├── Controllers     # Capa de presentación REST
├── Services        # Lógica de negocio
├── Repositories    # Acceso a datos
├── DTOs           # Objetos de transferencia
├── Entities       # Modelos de datos
├── Config         # Configuraciones
└── Utils          # Utilidades
```

## 🔧 Comandos de Desarrollo

### Compilar
```bash
./mvnw clean compile
```

### Ejecutar tests
```bash
./mvnw test
```

### Generar JAR
```bash
./mvnw package
```

### Ejecutar con perfil específico
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🐳 Docker

### Construir imagen
```bash
docker build -t mtd-api .
```

### Ejecutar contenedor
```bash
docker run -p 8080:8080 mtd-api
```

### Docker Compose (completo)
```bash
# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f app_mtd

# Detener servicios
docker-compose down
```

## 🔍 Monitoreo

Spring Boot Actuator proporciona endpoints de monitoreo:

- `/actuator/health` - Estado de la aplicación
- `/actuator/info` - Información de la aplicación
- `/actuator/metrics` - Métricas de rendimiento

## 🌐 Servicios Externos

### Cloudflare R2
Almacenamiento de archivos con API compatible con S3.

### Cloudflare D1
Base de datos SQLite distribuida para replicación de datos.

### Gmail SMTP
Servicio de correo electrónico para notificaciones.

## 🚀 Despliegue

### Producción
El proyecto incluye pipeline de CI/CD con Jenkins:

1. **Build** - Compilación y tests
2. **Docker** - Construcción de imagen
3. **Deploy** - Despliegue automático

### Variables de entorno requeridas
Asegúrate de configurar todas las variables de entorno en tu servidor de producción.

## 🤝 Contribuir

1. Fork del proyecto
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

### Estándares de código
- Usar Lombok para reducir boilerplate
- Seguir convenciones de Spring Boot
- Documentar endpoints con OpenAPI
- Escribir tests unitarios

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

## 👥 Equipo

- **Sauro DEV** - Desarrollo principal
- Email: SauroDev@hotmail.com

## 🆘 Soporte

Si encuentras algún problema:

1. Revisa la documentación
2. Consulta los logs: `docker-compose logs app_mtd`
3. Verifica el health check: `curl http://localhost:8080/actuator/health`
4. Crea un issue en GitHub
