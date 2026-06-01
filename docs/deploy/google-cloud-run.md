# Despliegue Backend Checkly En Google Cloud

Esta guia despliega solo el backend Quarkus en Google Cloud con Cloud Run, Cloud SQL MySQL, Artifact Registry, Secret Manager y Cloud Build.

## 1. Variables Locales

Ejecuta esto en PowerShell y reemplaza `TU_PROJECT_ID` por tu proyecto real.

```powershell
$PROJECT_ID = "TU_PROJECT_ID"
$REGION = "us-central1"
$SERVICE = "checkly-api"
$REPOSITORY = "checkly"
$IMAGE = "backend"
$SQL_INSTANCE = "checkly-mysql"
$DB_NAME = "checkly"
$DB_USER = "checkly_app"

gcloud auth login
gcloud config set project $PROJECT_ID
```

## 2. Habilitar APIs

```powershell
gcloud services enable run.googleapis.com
gcloud services enable artifactregistry.googleapis.com
gcloud services enable cloudbuild.googleapis.com
gcloud services enable sqladmin.googleapis.com
gcloud services enable secretmanager.googleapis.com
gcloud services enable iamcredentials.googleapis.com
```

## 3. Service Account De Cloud Run

```powershell
gcloud iam service-accounts create checkly-run `
  --display-name="Checkly Cloud Run runtime"

gcloud projects add-iam-policy-binding $PROJECT_ID `
  --member="serviceAccount:checkly-run@$PROJECT_ID.iam.gserviceaccount.com" `
  --role="roles/cloudsql.client"

gcloud projects add-iam-policy-binding $PROJECT_ID `
  --member="serviceAccount:checkly-run@$PROJECT_ID.iam.gserviceaccount.com" `
  --role="roles/secretmanager.secretAccessor"
```

Permisos para Cloud Build:

```powershell
$PROJECT_NUMBER = gcloud projects describe $PROJECT_ID --format="value(projectNumber)"
$CLOUD_BUILD_SA = "$PROJECT_NUMBER@cloudbuild.gserviceaccount.com"

gcloud projects add-iam-policy-binding $PROJECT_ID `
  --member="serviceAccount:$CLOUD_BUILD_SA" `
  --role="roles/artifactregistry.writer"

gcloud projects add-iam-policy-binding $PROJECT_ID `
  --member="serviceAccount:$CLOUD_BUILD_SA" `
  --role="roles/run.admin"

gcloud projects add-iam-policy-binding $PROJECT_ID `
  --member="serviceAccount:$CLOUD_BUILD_SA" `
  --role="roles/iam.serviceAccountUser"
```

Si Cloud Build falla por permisos y el error muestra otra service account, por ejemplo la Compute Engine default service account, repite los tres bindings anteriores usando esa cuenta como `$CLOUD_BUILD_SA`.

## 4. Artifact Registry

```powershell
gcloud artifacts repositories create $REPOSITORY `
  --repository-format=docker `
  --location=$REGION `
  --description="Checkly backend images"
```

## 5. Cloud SQL MySQL

```powershell
gcloud sql instances create $SQL_INSTANCE `
  --database-version=MYSQL_8_0 `
  --region=$REGION `
  --tier=db-g1-small

gcloud sql databases create $DB_NAME `
  --instance=$SQL_INSTANCE
```

Crea un password seguro y despues el usuario:

```powershell
$DB_PASSWORD = "TU_PASSWORD_SEGURO"

gcloud sql users create $DB_USER `
  --instance=$SQL_INSTANCE `
  --password=$DB_PASSWORD
```

Otorga permisos del esquema al usuario de la app. Puedes hacerlo desde Cloud SQL Studio o con un cliente MySQL conectado a la instancia:

```sql
GRANT ALL PRIVILEGES ON checkly.* TO 'checkly_app'@'%';
FLUSH PRIVILEGES;
```

Confirma el connection name:

```powershell
$CLOUDSQL_CONNECTION = gcloud sql instances describe $SQL_INSTANCE --format="value(connectionName)"
$CLOUDSQL_CONNECTION
```

Debe verse asi:

```text
PROJECT_ID:us-central1:checkly-mysql
```

## 6. Secret Manager

Guardar password de DB:

```powershell
$DB_PASSWORD | gcloud secrets create db-password --data-file=-
```

Descarga desde Firebase Console el JSON de Firebase Admin SDK y guardalo fuera del repo, por ejemplo:

```text
D:\secure\firebase-service-account.json
```

Crear secreto:

```powershell
gcloud secrets create firebase-service-account `
  --data-file="D:\secure\firebase-service-account.json"
```

## 7. Build Y Deploy

Desde `D:\ToDoFinal\Backend`:

```powershell
cd D:\ToDoFinal\Backend

gcloud builds submit . `
  --config cloudbuild.yaml `
  --substitutions _REGION=$REGION,_REPOSITORY=$REPOSITORY,_IMAGE=$IMAGE,_SERVICE=$SERVICE,_SQL_INSTANCE=$SQL_INSTANCE,_DB_NAME=$DB_NAME,_DB_USER=$DB_USER
```

El `cloudbuild.yaml` usa `${PROJECT_ID}` del proyecto activo de `gcloud`; no tiene un project id hardcodeado.

## 8. Obtener URL Y Validar

```powershell
$API_URL = gcloud run services describe $SERVICE `
  --region=$REGION `
  --format="value(status.url)"

$API_URL
curl "$API_URL/status"
```

Si falla:

```powershell
gcloud run services logs read $SERVICE `
  --region=$REGION `
  --limit=100
```

## 9. Probar Con Frontend Local

En `Frontend/.env`, cambia temporalmente:

```env
EXPO_PUBLIC_API_URL=https://TU_CLOUD_RUN_URL
```

Prueba manual:

- Login.
- Dashboard.
- Crear lista.
- Crear tarea.
- Search.
- Perfil.
- Cambio de idioma.

## 10. Notas Importantes

- Cloud Run queda publico, pero los endpoints protegidos siguen requiriendo Firebase ID Token.
- `CORS_ORIGINS=*` queda como default de esta fase.
- Flyway corre al iniciar con `DB_MIGRATE_AT_START=true`.
- No subas `.env`, `secrets/` ni JSON de Firebase Admin SDK.
- Expo/EAS queda fuera de esta fase; usa la URL de Cloud Run como `EXPO_PUBLIC_API_URL` cuando hagas el build movil.

## 11. Troubleshooting Rapido

- `Permission denied` en Cloud Build: revisa que la service account real del build tenga `roles/artifactregistry.writer`, `roles/run.admin` y `roles/iam.serviceAccountUser`.
- Error leyendo Firebase: confirma que existe el secreto `firebase-service-account` y que Cloud Run monta `/secrets/firebase-service-account.json`.
- Error conectando a MySQL: confirma que Cloud Run tenga `--add-cloudsql-instances` y que `checkly-run` tenga `roles/cloudsql.client`.
- Error de Flyway: revisa logs de Cloud Run y valida que la base `checkly` este vacia o tenga historial compatible.
