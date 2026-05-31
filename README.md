# Checkly Backend

Backend Quarkus para Checkly. Expone una API REST protegida con Firebase ID Token, persiste datos en MySQL y usa Flyway para versionar el esquema.

## Estado Actual

- Quarkus 3.34 con Java 21.
- Firebase Admin SDK para verificar tokens.
- `/status` publico.
- Endpoints protegidos por `Authorization: Bearer <firebase-id-token>`.
- `POST /user` sincroniza el perfil backend usando el usuario autenticado en Firebase.
- `GET /me` y `PUT /me` para perfil.
- CRUD de task lists.
- CRUD de tasks/todos.
- Busqueda de listas y tareas.
- Ownership por usuario autenticado.
- MySQL con Flyway.
- Tests con H2 y filtros mock.
- Configuracion para Render en `render.yaml`.

## Requisitos

- Java 21.
- MySQL 8.
- Maven Wrapper incluido.
- Proyecto Firebase.
- Service account JSON de Firebase Admin SDK.

## Base de Datos

Crear base vacia:

```sql
CREATE DATABASE checkly CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Flyway ejecuta las migraciones al iniciar:

```text
src/main/resources/db/migration/
|-- V1__create_checkly_schema.sql
|-- V2__add_user_preferred_language.sql
```

Tablas actuales:

- `users`
- `task_lists`
- `todos`
- `flyway_schema_history`

Relaciones:

- `users -> task_lists`
- `users -> todos`
- `task_lists -> todos`

`due_date` guarda fecha y hora de vencimiento en un solo `DATETIME(6)`.

## Variables de Entorno

Copia el ejemplo:

```bash
copy .env.example .env
```

Variables:

```env
DB_KIND=mysql
DB_USERNAME=root
DB_PASSWORD=change-me
DB_JDBC_URL=jdbc:mysql://localhost:3306/checkly
DB_SCHEMA_STRATEGY=validate
DB_MIGRATE_AT_START=true
DB_BASELINE_ON_MIGRATE=true
FIREBASE_SERVICE_ACCOUNT_LOCATION=C:/secure-path/firebase-service-account.json
APP_VERSION=dev
PORT=8080
```

Opcional para CORS:

```env
CORS_ORIGINS=*
```

No subas `.env`, carpetas `secrets/` ni archivos JSON de service account.

## Ejecutar en Desarrollo

Windows:

```bash
mvnw.cmd quarkus:dev
```

Linux/macOS:

```bash
./mvnw quarkus:dev
```

La API escucha en:

```text
http://localhost:8080
```

Status:

```bash
curl http://localhost:8080/status
```

## Build y Tests

Tests:

```bash
mvnw.cmd test
```

Build JVM:

```bash
mvnw.cmd package -DskipTests
```

Ejecutar jar:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## Endpoints

Todos excepto `/status` requieren:

```http
Authorization: Bearer <firebase-id-token>
```

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `GET` | `/status` | Estado de API |
| `POST` | `/user` | Crea/sincroniza perfil backend |
| `GET` | `/me` | Perfil autenticado |
| `PUT` | `/me` | Actualiza perfil: `fullName`, `email`, `preferredLanguage` |
| `GET` | `/task-lists` | Lista task lists del usuario |
| `POST` | `/task-lists` | Crea task list |
| `GET` | `/task-lists/{id}` | Obtiene task list |
| `PUT` | `/task-lists/{id}` | Actualiza task list |
| `DELETE` | `/task-lists/{id}` | Elimina task list y tareas |
| `GET` | `/task-lists/{id}/tasks` | Lista tareas de una task list |
| `POST` | `/task-lists/{id}/tasks` | Crea tarea |
| `GET` | `/tasks/{id}` | Obtiene tarea |
| `PUT` | `/tasks/{id}` | Actualiza tarea |
| `DELETE` | `/tasks/{id}` | Elimina tarea |
| `GET` | `/search?q=...` | Busca listas y tareas del usuario |

Existe tambien un recurso legacy `/todo` usado por pruebas antiguas; la app actual no depende de el.

## Payloads Principales

### `POST /user`

```json
{
  "fullName": "Alex Gonzalez",
  "email": "alex@example.com"
}
```

El `firebaseUuid` se toma del token, no del body.

### `PUT /me`

```json
{
  "fullName": "Alex Gonzalez",
  "email": "alex@example.com",
  "preferredLanguage": "es"
}
```

### `POST /task-lists`

```json
{
  "title": "Work",
  "description": "Tasks and priorities",
  "accentColor": "#0B72E7",
  "icon": "book"
}
```

### `POST /task-lists/{id}/tasks`

```json
{
  "title": "Prepare report",
  "description": "Send first draft",
  "priority": "medium",
  "dueDate": "2026-05-31T18:30:00.000Z",
  "completed": false
}
```

## Errores

El backend responde errores con `message` para que el frontend muestre mensajes amigables. Casos esperados:

- `401`: sesion expirada o token invalido.
- `403`: accion no permitida.
- `404`: recurso no encontrado o no pertenece al usuario.
- `400`: validacion invalida.
- `500`: error inesperado.

## Seguridad

- El backend no almacena passwords.
- Firebase Authentication gestiona credenciales.
- El backend verifica tokens con Firebase Admin SDK.
- Cada query de listas/tareas se limita al usuario autenticado.
- Las task lists eliminan sus tareas por cascade.

## Deploy en Render

El archivo `render.yaml` contiene una configuracion base:

```yaml
buildCommand: ./mvnw -DskipTests package
startCommand: java -jar target/quarkus-app/quarkus-run.jar
```

Variables recomendadas:

- `DB_KIND=mysql`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_JDBC_URL`
- `DB_SCHEMA_STRATEGY=validate`
- `DB_MIGRATE_AT_START=true`
- `DB_BASELINE_ON_MIGRATE=true`
- `FIREBASE_SERVICE_ACCOUNT_LOCATION`
- `CORS_ORIGINS`
- `APP_VERSION=render`
- `PORT`

Para Firebase Admin SDK en produccion, monta el JSON como secret/archivo y apunta `FIREBASE_SERVICE_ACCOUNT_LOCATION` a esa ruta.

## Notas de Migraciones

- No edites migraciones ya aplicadas en bases compartidas o de produccion.
- En desarrollo local, si decides borrar la base, Flyway volvera a aplicar desde `V1`.
- Para cambios nuevos de esquema, agrega una nueva migracion `V{n}__descripcion.sql`.

## Troubleshooting

- Si MySQL no esta en PATH, puedes usar Workbench, DBeaver o la ruta completa a `mysql.exe`; Quarkus no necesita que el cliente `mysql` este en PATH.
- Si Flyway dice `Schema is up to date`, las migraciones ya fueron aplicadas.
- Si Firebase no inicializa, revisa `FIREBASE_SERVICE_ACCOUNT_LOCATION`.
- Si Expo Go no conecta, revisa CORS y que el frontend apunte a la IP LAN correcta.
