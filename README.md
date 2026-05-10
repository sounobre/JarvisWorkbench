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