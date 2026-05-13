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