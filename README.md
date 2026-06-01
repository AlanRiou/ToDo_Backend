# Checkly - Backend API

Checkly es una aplicacion movil para gestionar listas de tareas, tareas pendientes, prioridades y busqueda de actividades por usuario. El sistema usa Firebase Authentication para autenticar usuarios, un backend Quarkus para exponer la API REST, MySQL para persistencia y Google Cloud Run para el despliegue publico del backend.

Este repositorio contiene el backend de Checkly. La API valida Firebase ID Tokens como JWT, protege los recursos por usuario autenticado y expone endpoints para perfil, listas, tareas y busqueda. Los datos se persisten en MySQL y el esquema se versiona con Flyway.

## Tecnologias Utilizadas

- Java 21
- Quarkus 3.34
- Hibernate ORM con Panache
- Hibernate Validator
- MySQL 8
- Flyway
- Firebase Admin SDK
- JUnit 5 / Rest Assured
- Docker
- Google Cloud Run
- Google Cloud SQL
- Google Artifact Registry
- Google Secret Manager
- Google Cloud Build

## Instalacion Y Configuracion

Sigue estos pasos en orden para ejecutar el backend localmente.

### 1. Requisitos

- Java 21.
- MySQL 8.
- Proyecto Firebase con Authentication habilitado.
- JSON de service account de Firebase Admin SDK.

### 2. Crear base de datos local

Crea una base vacia en MySQL llamada `checkly`. Flyway ejecutara las migraciones al iniciar la aplicacion.

```sql
CREATE DATABASE checkly CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Crear `.env`

Crea `Backend/.env` copiando este contenido y ajustando `DB_PASSWORD` y `FIREBASE_SERVICE_ACCOUNT_LOCATION` a tu maquina.

```env
DB_KIND=mysql
DB_USERNAME=root
DB_PASSWORD=change-me
DB_JDBC_URL=jdbc:mysql://localhost:3306/checkly
DB_SCHEMA_STRATEGY=validate
DB_MIGRATE_AT_START=true
DB_BASELINE_ON_MIGRATE=true
CORS_ORIGINS=*
FIREBASE_SERVICE_ACCOUNT_LOCATION=C:/secure-path/firebase-service-account.json
APP_VERSION=dev
PORT=8080
```

El archivo Firebase service account no se incluye en el repositorio porque contiene credenciales privadas. Debe descargarse desde Firebase Console y guardarse fuera de git. La variable `FIREBASE_SERVICE_ACCOUNT_LOCATION` debe apuntar a ese archivo.

### 4. Instalar dependencias y compilar

Desde `Backend/`:

```powershell
.\mvnw.cmd -DskipTests package
```

### 5. Ejecutar el backend

Desde `Backend/`:

```powershell
.\mvnw.cmd quarkus:dev
```

La API local queda disponible en `http://localhost:8080`.

### 6. Ejecutar tests

```powershell
.\mvnw.cmd test
```

## Links Desplegados

URL publica oficial del backend:

- API base: [https://checkly-api-631651477281.us-central1.run.app](https://checkly-api-631651477281.us-central1.run.app)
- Health check: [https://checkly-api-631651477281.us-central1.run.app/status](https://checkly-api-631651477281.us-central1.run.app/status)

## Usuarios De Prueba

Usuario disponible para evaluacion:

- Correo: `alex@dev.mx`
- Contrasenia: `Password123`

## Arquitectura

El backend utiliza una arquitectura limpia/hexagonal ligera. La regla principal es separar la logica de negocio de los detalles externos como REST, Firebase, MySQL y Quarkus. No es una implementacion purista de Clean Architecture, porque se apoya en Quarkus y Panache para simplificar persistencia, pero si mantiene las responsabilidades principales separadas por capas.

```text
Backend/
|-- src/main/java/com/itesm/
|   |-- domain/              # Modelos de dominio y contratos de repositorio
|   |-- application/         # DTOs, seguridad de aplicacion y casos de uso
|   |-- infrastructure/      # Firebase, persistencia, mappers y adaptadores tecnicos
|   |-- interfaces/          # Controladores REST
|-- src/main/resources/      # application.properties y migraciones Flyway
|-- src/test/                # Tests automatizados
|-- Dockerfile               # Imagen JVM runtime-only
|-- cloudbuild.yaml          # Build, push y deploy a Cloud Run
|-- docs/deploy/             # Guia de despliegue
```

Patrones y decisiones usadas:

- Clean/Hexagonal Architecture ligera: `domain` no depende de REST ni de Firebase.
- Use Case Pattern: la logica de aplicacion vive en `application/usecase`.
- Repository Pattern: `domain/repository` define contratos y `infrastructure/persistence/repository` los implementa.
- DTO Pattern: los objetos de entrada/salida HTTP viven en `application/dto`.
- Mapper Pattern: `infrastructure/mapper` traduce entre entidades de persistencia, dominio y respuestas.
- Adapter Pattern: Firebase y MySQL quedan encapsulados en `infrastructure`.
- Controller/Resource Pattern: `interfaces/rest` expone la API HTTP.

## Despliegue En Google Cloud

El backend esta preparado para desplegarse con Cloud Run, Cloud SQL MySQL, Artifact Registry, Secret Manager y Cloud Build.

Comando de redeploy desde `Backend/`:

```powershell
gcloud builds submit . `
  --config cloudbuild.yaml `
  --substitutions _REGION=us-central1,_REPOSITORY=checkly,_IMAGE=backend,_SERVICE=checkly-api
```

La guia manual completa esta en `docs/deploy/google-cloud-run.md`.

## Endpoints Principales

Todos excepto `/status` requieren:

```http
Authorization: Bearer <firebase-id-token>
```

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `GET` | `/status` | Health check publico. |
| `POST` | `/user` | Crea o sincroniza perfil backend. |
| `GET` | `/me` | Obtiene perfil autenticado. |
| `PUT` | `/me` | Actualiza perfil. |
| `GET` | `/task-lists` | Lista las listas del usuario. |
| `POST` | `/task-lists` | Crea una lista. |
| `GET` | `/task-lists/{id}` | Obtiene una lista. |
| `PUT` | `/task-lists/{id}` | Actualiza una lista. |
| `DELETE` | `/task-lists/{id}` | Elimina una lista y sus tareas. |
| `GET` | `/task-lists/{id}/tasks` | Lista tareas de una lista. |
| `POST` | `/task-lists/{id}/tasks` | Crea una tarea. |
| `GET` | `/tasks/{id}` | Obtiene una tarea. |
| `PUT` | `/tasks/{id}` | Actualiza una tarea. |
| `DELETE` | `/tasks/{id}` | Elimina una tarea. |
| `GET` | `/search?q=...` | Busca listas y tareas del usuario. |

## Seguridad

- El backend no almacena passwords.
- Firebase Authentication administra credenciales.
- Firebase Admin SDK verifica los ID Tokens.
- Cada recurso se filtra por usuario autenticado.
- Los secretos de produccion se manejan con Secret Manager.
