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

## Arquitectura

El backend sigue una separacion por responsabilidades dentro de una API REST Quarkus.

```text
Backend/
|-- src/main/java/           # Recursos REST, servicios, modelos, repositorios y seguridad
|-- src/main/resources/      # application.properties y migraciones Flyway
|-- src/test/                # Tests automatizados
|-- Dockerfile               # Imagen JVM runtime-only
|-- cloudbuild.yaml          # Build, push y deploy a Cloud Run
|-- docs/deploy/             # Guia de despliegue
```

Flujo principal:

1. El frontend obtiene un Firebase ID Token.
2. El token llega en `Authorization: Bearer <token>`.
3. El backend valida el token con Firebase Admin SDK.
4. La API resuelve el usuario autenticado.
5. Las consultas de listas y tareas se filtran por ese usuario.
6. Los cambios se guardan en MySQL.

## Instalacion

Requisitos:

- Java 21.
- MySQL 8.
- Proyecto Firebase con Authentication habilitado.
- JSON de service account de Firebase Admin SDK.

Desde `Backend/`:

```powershell
.\mvnw.cmd -DskipTests package
```

## Base De Datos Local

Crea una base vacia en MySQL llamada `checkly`. Flyway ejecuta las migraciones al iniciar la aplicacion.

Migraciones:

```text
src/main/resources/db/migration/
```

Tablas principales:

- `users`
- `task_lists`
- `todos`
- `flyway_schema_history`

## Variables De Entorno

Copia `.env.example` como `.env` y completa los valores:

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

No subas `.env`, passwords ni archivos JSON de service account.

## Ejecutar El Proyecto

Desde `Backend/`:

```powershell
.\mvnw.cmd quarkus:dev
```

La API local queda disponible en `http://localhost:8080`.

## Tests Y Build

Ejecutar tests:

```powershell
.\mvnw.cmd test
```

Generar build JVM:

```powershell
.\mvnw.cmd -DskipTests package
```

## Links Desplegados

URL publica oficial del backend:

- API base: [https://checkly-api-631651477281.us-central1.run.app](https://checkly-api-631651477281.us-central1.run.app)
- Health check: [https://checkly-api-631651477281.us-central1.run.app/status](https://checkly-api-631651477281.us-central1.run.app/status)

## Usuarios De Prueba

Usuario disponible para evaluacion:

- Correo: `alex@dev.mx`
- Contrasenia: `Password123`

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
