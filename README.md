# Jarvis Workbench API

API local para processamento de textos, execução de jobs, alinhamento bilíngue e experimentos com IA local.

## Stack

- Java 21
- Spring Boot 3.5.x
- Maven

## Como rodar

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```
## Endpoints

### Health Check
```
GET http://localhost:8088/api/health
```
Resposta esperada:

```json
{
  "status": "UP",
  "service": "jarvis-workbench-api"
}
```

### Create Job
```
curl --request POST \
  --url http://localhost:8088/api/jobs \
  --header 'Content-Type: application/json' \
  --data '{
  "type": "EPUB_IMPORT",
  "metadataJson": "{\"source\":\"manual-test\"}"
}'
```
Resposta esperada:

```json
{
  "id": "job_23562c6a89b94625a5063438026aa2b5",
  "type": "EPUB_IMPORT",
  "status": "PENDING",
  "progress": 0,
  "currentStep": null,
  "totalSteps": null,
  "message": null,
  "metadataJson": "{\"source\":\"manual-test\"}",
  "createdAt": "2026-05-12T13:26:37.2508208",
  "startedAt": null,
  "finishedAt": null,
  "updatedAt": "2026-05-12T13:26:37.2508208",
  "errorCode": null,
  "errorMessage": null
}
```
### Get Jobs by Public Id
```
curl --request GET \
  --url http://localhost:8088/api/jobs/job_23562c6a89b94625a5063438026aa2b5
```
Resposta esperada:

```json
{
  "id": "job_23562c6a89b94625a5063438026aa2b5",
  "type": "EPUB_IMPORT",
  "status": "PENDING",
  "progress": 0,
  "currentStep": null,
  "totalSteps": null,
  "message": null,
  "metadataJson": "{\"source\":\"manual-test\"}",
  "createdAt": "2026-05-12T13:26:37.250821",
  "startedAt": null,
  "finishedAt": null,
  "updatedAt": "2026-05-12T13:26:37.250821",
  "errorCode": null,
  "errorMessage": null
}
```
### Get All Jobs (paginated)
```
curl --request GET \
  --url 'http://localhost:8088/api/jobs?status=PENDING&type=EPUB_IMPORT'
```
Resposta esperada
```json
{
  "items": [
    {
      "id": "job_23562c6a89b94625a5063438026aa2b5",
      "type": "EPUB_IMPORT",
      "status": "PENDING",
      "progress": 0,
      "currentStep": null,
      "totalSteps": null,
      "message": null,
      "metadataJson": "{\"source\":\"manual-test\"}",
      "createdAt": "2026-05-12T13:26:37.250821",
      "startedAt": null,
      "finishedAt": null,
      "updatedAt": "2026-05-12T13:26:37.250821",
      "errorCode": null,
      "errorMessage": null
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```
### Start Job
```
curl --request POST \
  --url http://localhost:8088/api/jobs/job_d5204a1b9ac945959c4df2df55c22318/start \
  --header 'Content-Type: application/json' \
  --data '{
  "totalSteps": 10,
  "message": "Starting EPUB import"
}'
```
Resposta esperada
```json
{
  "id": "job_d5204a1b9ac945959c4df2df55c22318",
  "type": "EPUB_IMPORT",
  "status": "RUNNING",
  "progress": 0,
  "currentStep": 0,
  "totalSteps": 10,
  "message": "Starting EPUB import",
  "metadataJson": "{\"source\":\"manual-test\"}",
  "createdAt": "2026-05-15T15:27:57.76384",
  "startedAt": "2026-05-15T15:28:00.7264551",
  "finishedAt": null,
  "updatedAt": "2026-05-15T15:28:00.7264551",
  "errorCode": null,
  "errorMessage": null
}
```
### Updated Job Progress
```
curl --request PATCH \
  --url http://localhost:8088/api/jobs/job_3883c574f4e94a8783bab582a8c633a9/progress \
  --header 'Content-Type: application/json' \
  --data '{
  "progress": 35,
  "currentStep": 3,
  "totalSteps": 10,
  "message": "Extracting chapters"
}'
```
Resposta esperada
```json
{
  "id": "job_3883c574f4e94a8783bab582a8c633a9",
  "type": "EPUB_IMPORT",
  "status": "RUNNING",
  "progress": 35,
  "currentStep": 3,
  "totalSteps": 10,
  "message": "Extracting chapters",
  "metadataJson": "{\"source\":\"manual-test\"}",
  "createdAt": "2026-05-15T18:16:34.603573",
  "startedAt": "2026-05-15T18:16:38.868631",
  "finishedAt": null,
  "updatedAt": "2026-05-15T18:16:50.3612025",
  "errorCode": null,
  "errorMessage": null
}
```
### Completd Job
```
curl --request POST \
  --url http://localhost:8088/api/jobs/job_3883c574f4e94a8783bab582a8c633a9/complete \
  --header 'Content-Type: application/json' \
  --data '{
  "message": "Job completed successfully"
}'
```
Resposta esperada
```json
{
  "id": "job_3883c574f4e94a8783bab582a8c633a9",
  "type": "EPUB_IMPORT",
  "status": "COMPLETED",
  "progress": 100,
  "currentStep": 3,
  "totalSteps": 10,
  "message": "Job completed successfully",
  "metadataJson": "{\"source\":\"manual-test\"}",
  "createdAt": "2026-05-15T18:16:34.603573",
  "startedAt": "2026-05-15T18:16:38.868631",
  "finishedAt": "2026-05-15T18:18:24.5482539",
  "updatedAt": "2026-05-15T18:18:24.5482539",
  "errorCode": null,
  "errorMessage": null
}
```
### Fail Job
```
curl --request POST \
  --url http://localhost:8088/api/jobs/job_5b8dfaddb45c437e9e36eedcceb66093/fail \
  --header 'Content-Type: application/json' \
  --data '{
  "errorCode": "EPUB_PARSE_ERROR",
  "errorMessage": "Unable to read EPUB spine"
}'
```
Resposta esperada
```json
{
  "id": "job_5b8dfaddb45c437e9e36eedcceb66093",
  "type": "EPUB_IMPORT",
  "status": "FAILED",
  "progress": 0,
  "currentStep": 0,
  "totalSteps": 10,
  "message": "Starting EPUB import",
  "metadataJson": "{\"source\":\"manual-test\"}",
  "createdAt": "2026-05-15T18:19:20.137946",
  "startedAt": "2026-05-15T18:19:23.293639",
  "finishedAt": "2026-05-15T18:19:26.8488543",
  "updatedAt": "2026-05-15T18:19:26.8488543",
  "errorCode": "EPUB_PARSE_ERROR",
  "errorMessage": "Unable to read EPUB spine"
}
```
### Cancel Job
```
curl --request POST \
  --url http://localhost:8088/api/jobs/job_621a322fca174f9796e7fa5698b65c9e/cancel \
  --header 'Content-Type: application/json' \
  --data '{
  "message": "Cancelled by user"
}'
```
Resposta esperada
```json
{
  "id": "job_621a322fca174f9796e7fa5698b65c9e",
  "type": "EPUB_IMPORT",
  "status": "CANCELLED",
  "progress": 0,
  "currentStep": 0,
  "totalSteps": 10,
  "message": "Cancelled by user",
  "metadataJson": "{\"source\":\"manual-test\"}",
  "createdAt": "2026-05-15T18:20:39.365874",
  "startedAt": "2026-05-15T18:20:43.354565",
  "finishedAt": "2026-05-15T18:20:46.6276384",
  "updatedAt": "2026-05-15T18:20:46.6276384",
  "errorCode": null,
  "errorMessage": null
}
```
## Banco de dados local

O projeto usa PostgreSQL.

Configuração padrão local:

- Database: `jarvis_workbench`
- Username: definido pela variável `JARVIS_DB_USERNAME`
- Password: definido pela variável `JARVIS_DB_PASSWORD`
- Port: `5432`
  Exemplo local usado durante desenvolvimento:

```bash
JARVIS_DB_USERNAME=postgres
JARVIS_DB_PASSWORD=123456
```
SQL inicial:

```sql
CREATE DATABASE jarvis_workbench;
```

## Padrão de erros da API

Todas as respostas de erro seguem o formato:

```json
{
  "timestamp": "2026-05-10T12:00:00",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Invalid request data",
  "path": "/api/example",
  "fields": [
    {
      "name": "sourceLang",
      "message": "must not be blank"
    }
  ]
}
```

Quando não houver erro de campo, `fields` será retornado como lista vazia:

```json
{
  "timestamp": "2026-05-10T12:00:00",
  "status": 404,
  "error": "RESOURCE_NOT_FOUND",
  "message": "Resource not found",
  "path": "/api/example/123",
  "fields": []
}
```