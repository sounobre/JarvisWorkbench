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