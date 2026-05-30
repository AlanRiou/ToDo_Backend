# Checkly

Checkly es una aplicacion movil de gestion de tareas construida con Expo, React Native y un backend propio en Quarkus. La app organiza tareas por listas, muestra prioridades del dia, permite navegar a detalles, crear/editar listas, crear/editar tareas, buscar elementos y administrar el perfil del usuario.

Este README se mantiene como copia principal del proyecto y tambien se replica en `Frontend/README.md` y `Backend/README.md` para que la informacion este disponible desde cualquier carpeta.

## Estado Actual

### Frontend

- App Expo/React Native con Expo Router.
- Marca visual actual: `Checkly`.
- Flujo mock navegable listo para Expo Go:
  - Login.
  - Registro.
  - Dashboard.
  - Search.
  - Detalle de lista.
  - New List.
  - New/Edit Task.
  - Perfil.
  - Pantallas para cambiar nombre, correo y password.
- Componentes reutilizables creados para pantallas y Storybook:
  - `BrandHeader`, `BottomNav`, `ProfileAvatar`.
  - `TaskProgressCard`, `TaskRow`, `CourseHeroCard`.
  - `AuthTextField`, `Button`, `IconButton`.
  - `ConfirmDialog`, estados, notices y controles.
- Storybook web configurado para revisar componentes.
- Storybook native/on-device disponible para Expo.
- La logica real de API/Firebase todavia esta pendiente de conectarse en pantallas.

### Backend

- Backend Quarkus 3 con Java 21.
- Persistencia con MySQL y Hibernate/Panache.
- Firebase Admin SDK para registro/validacion de usuarios.
- Dominio base:
  - `User`.
  - `TaskList`.
  - `Todo`/`Task`.
- API REST para:
  - usuario actual.
  - registro.
  - listas.
  - tareas.
  - busqueda.
  - status.
- Filtros de autenticacion Firebase y contexto de usuario.
- Pendiente principal: conectar frontend real contra estos endpoints y preparar despliegue final.

## Tecnologias

### Frontend

- Expo 54.
- React Native.
- Expo Router.
- TypeScript.
- NativeWind.
- Gluestack UI.
- Lucide React Native.
- Storybook.
- Axios.
- Firebase Client SDK.

### Backend

- Java 21.
- Quarkus 3.34.3.
- Quarkus REST con Jackson.
- Maven Wrapper.
- MySQL.
- Conector opcional para Google Cloud SQL MySQL.
- H2 para tests.
- Firebase Admin SDK.
- Docker.

## Estructura

```text
ToDoFinal/
|-- Frontend/
|   |-- app/                  # Rutas Expo Router
|   |-- components/           # Componentes reutilizables
|   |-- constants/            # Tokens visuales y tipografia
|   |-- context/              # Contextos de app/auth
|   |-- lib/                  # Configuracion Firebase/API
|   |-- services/             # Servicios HTTP planeados
|   |-- stories/              # Stories de Storybook
|   |-- package.json
|
|-- Backend/
|   |-- src/main/java/com/itesm/
|   |   |-- application/      # DTOs, seguridad y casos de uso
|   |   |-- domain/           # Modelos y repositorios
|   |   |-- infrastructure/   # Firebase, JPA y persistencia
|   |   |-- interfaces/rest/  # Recursos REST
|   |-- src/main/resources/
|   |-- src/test/
|   |-- Dockerfile
|   |-- pom.xml
|
|-- README.md
```

## Requisitos

- Node.js y npm.
- Expo Go en dispositivo movil, o emulador Android/iOS.
- Java 21.
- MySQL 8.
- Proyecto Firebase con Authentication habilitado.
- Llave JSON de Firebase Admin SDK para el backend.

## Frontend: Instalacion y Uso

Desde la raiz:

```bash
cd Frontend
npm install
```

Iniciar Expo:

```bash
npm run start
```

Abrir en Expo Go escaneando el QR.

Otros comandos:

```bash
npm run android
npm run ios
npm run web
npm run lint
```

Storybook web:

```bash
npm run storybook
```

Storybook on-device con Expo:

```bash
npm run storybook:native
```

Validacion TypeScript:

```bash
node .\node_modules\typescript\bin\tsc --noEmit
```

## Backend: Configuracion

Crear base de datos MySQL:

```sql
CREATE DATABASE todogrupo1 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Entrar al backend:

```bash
cd Backend
```

Crear `.env` desde el ejemplo:

```bash
cp .env.example .env
```

Variables esperadas:

```env
DB_KIND=mysql
DB_USERNAME=root
DB_PASSWORD=your-mysql-password
DB_JDBC_URL=jdbc:mysql://localhost:3306/todogrupo1
DB_SCHEMA_STRATEGY=update
FIREBASE_SERVICE_ACCOUNT_LOCATION=C:/secure-path/firebase-service-account.json
APP_VERSION=dev
PORT=8080
```

La llave de Firebase Admin SDK no debe subirse al repositorio.

## Backend: Ejecutar

Windows:

```bash
mvnw.cmd quarkus:dev
```

Linux/macOS:

```bash
./mvnw quarkus:dev
```

La API corre por defecto en:

```text
http://localhost:8080
```

Build:

```bash
mvnw.cmd package -DskipTests
java -jar target/quarkus-app/quarkus-run.jar
```

Tests:

```bash
mvnw.cmd test
```

## Endpoints Principales

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `GET` | `/status` | Estado de la API |
| `POST` | `/user` | Registro backend despues de crear/registrar usuario |
| `GET` | `/me` | Usuario autenticado |
| `GET` | `/task-lists` | Listar listas del usuario |
| `POST` | `/task-lists` | Crear lista |
| `GET` | `/task-lists/{id}` | Obtener detalle de lista |
| `PUT` | `/task-lists/{id}` | Actualizar lista |
| `DELETE` | `/task-lists/{id}` | Eliminar lista |
| `GET` | `/task-lists/{id}/tasks` | Listar tareas de una lista |
| `POST` | `/task-lists/{id}/tasks` | Crear tarea en lista |
| `GET` | `/tasks/{id}` | Obtener tarea |
| `PUT` | `/tasks/{id}` | Actualizar tarea |
| `DELETE` | `/tasks/{id}` | Eliminar tarea |
| `GET` | `/search?q=...` | Buscar listas/tareas |

Los endpoints protegidos esperan:

```http
Authorization: Bearer <firebase-id-token>
```

## Flujo Frontend Actual

1. La app inicia en login.
2. `Login` redirige al dashboard mock.
3. `Create Account` abre registro.
4. En dashboard se muestran listas mock y tareas del dia.
5. Al tocar una lista se abre el detalle.
6. En detalle, `Lists` regresa al dashboard.
7. Los tres puntos de una tarea abren opciones superpuestas:
   - Edit Task.
   - Delete Task.
8. Delete Task abre confirmacion modal.
9. El avatar abre perfil.
10. En perfil se puede navegar a pantallas de cambio de nombre, correo y password.
11. Logout regresa al login.

## Pendientes Antes de Entrega

- Conectar Firebase Authentication real en login/register.
- Guardar sesion y token Firebase.
- Conectar Axios con `EXPO_PUBLIC_API_URL`.
- Enviar `Authorization: Bearer <token>` desde interceptor.
- Reemplazar mocks por llamadas reales:
  - listas.
  - tareas.
  - busqueda.
  - perfil.
- Conectar cambios de cuenta con Firebase/backend.
- Desplegar backend en Render/Railway/Fly.io.
- Probar app apuntando al backend desplegado.
- Revisar que no existan secretos locales versionados.

## Comandos de Verificacion Usados

Frontend:

```bash
node .\node_modules\typescript\bin\tsc --noEmit
```

Storybook:

```bash
npm run storybook
npm run storybook:native
```

Backend:

```bash
mvnw.cmd test
mvnw.cmd package -DskipTests
```

## Notas de Seguridad

- No subir `.env`.
- No subir service accounts de Firebase.
- No guardar passwords reales en mocks.
- En produccion, usar variables de entorno o secretos del proveedor de deploy.
