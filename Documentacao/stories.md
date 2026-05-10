# Backlog de Desenvolvimento — Trilha Java Backend + IA Local

## Objetivo

Construir um portfólio forte e realista para empregabilidade como **Desenvolvedor Java Backend / Fullstack Java**, usando um projeto principal com cara de produto: um sistema local inspirado no Jarvis, mas organizado como plataforma profissional.

A ideia é desenvolver em etapas, como se fosse um projeto real de empresa:

1. Backend Java/Spring Boot sólido.
2. Banco PostgreSQL bem modelado.
3. Jobs assíncronos e rastreáveis.
4. Testes automatizados.
5. Frontend React/TypeScript funcional.
6. Integração com IA local como diferencial.
7. Documentação profissional no GitHub.

---

# Produto-base sugerido

## Nome do projeto

**Jarvis Workbench**

## Descrição curta

Sistema local para processamento de textos, execução de jobs longos, alinhamento bilíngue, memória RAG e tradução assistida por IA local.

## Descrição para currículo

Plataforma em Java/Spring Boot para ingestão, processamento assíncrono e análise de textos bilíngues, com PostgreSQL, controle de jobs, logs, validações, busca semântica e integração com modelos locais via API.

---

# Stack recomendada

## Backend

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Validation
* Spring Security, em fase posterior
* PostgreSQL
* Flyway
* JUnit 5
* Mockito
* Testcontainers
* OpenAPI/Swagger

## Frontend

* React
* TypeScript
* Tailwind CSS
* Shadcn/ui
* React Hook Form
* Zod
* TanStack Query

## IA local / dados

* Ollama ou outro servidor local compatível com HTTP
* pgvector em fase posterior
* embeddings locais
* PostgreSQL JSONB para metadados

---

# Padrão de desenvolvimento

## Definição de pronto geral

Uma estória só é considerada pronta quando tiver:

* endpoint funcional;
* validação de entrada;
* tratamento de erro;
* persistência correta;
* teste unitário ou de integração quando fizer sentido;
* documentação mínima no README ou Swagger;
* commits pequenos e claros;
* nenhum código morto ou gambiarra sem justificativa.

## Padrão de branches

* `main`: versão estável.
* `develop`: integração.
* `feature/JW-001-nome-da-feature`: desenvolvimento de estória.
* `fix/JW-xxx-nome-do-fix`: correção.

## Padrão de commits

Exemplos:

* `feat: create job entity and repository`
* `feat: add job creation endpoint`
* `test: add job service tests`
* `fix: validate invalid job type`
* `docs: update README with job API examples`

---

# Épico 0 — Fundação do projeto

## Objetivo

Criar a base profissional do projeto, com estrutura limpa, banco configurado, migrations, documentação inicial e ambiente preparado para evoluir.

---

## JW-001 — Criar projeto Spring Boot base

### Estória

Como desenvolvedor, quero criar a estrutura inicial do backend em Spring Boot para ter uma base organizada e pronta para evolução.

### Regras de negócio

* O projeto deve iniciar sem erro.
* Deve ter um endpoint simples de health check.
* A estrutura deve separar camadas de controller, service, repository, entity e dto.

### Critérios de aceite

* Aplicação sobe localmente.
* Endpoint `GET /api/health` retorna status `200`.
* Resposta esperada:

```json
{
  "status": "UP",
  "service": "jarvis-workbench-api"
}
```

### Tarefas técnicas

* Criar projeto com Java 21 e Spring Boot.
* Adicionar dependências principais.
* Criar pacote base.
* Criar `HealthController`.
* Criar README inicial.

### O que quero ver no code review

* Pacotes bem nomeados.
* Sem regra de negócio no controller.
* README explicando como rodar.

---

## JW-002 — Configurar PostgreSQL e Flyway

### Estória

Como desenvolvedor, quero configurar PostgreSQL e Flyway para versionar o banco de dados de forma profissional.

### Regras de negócio

* Nenhuma tabela deve ser criada manualmente fora de migration.
* O sistema deve falhar ao iniciar se o banco estiver inacessível.
* Toda alteração estrutural no banco deve virar uma migration.

### Critérios de aceite

* Aplicação conecta no PostgreSQL.
* Flyway executa migration inicial.
* Existe uma tabela inicial chamada `app_schema_version_test` ou equivalente simples para validar a migração.

### Tarefas técnicas

* Configurar datasource.
* Adicionar Flyway.
* Criar migration `V1__init.sql`.
* Atualizar README com instruções de banco.

### O que quero ver no code review

* Nada de `spring.jpa.hibernate.ddl-auto=create`.
* Configuração separada por profile.
* Senhas fora do código.

---

## JW-003 — Padronizar resposta de erro

### Estória

Como consumidor da API, quero receber erros em formato padronizado para conseguir entender e tratar falhas corretamente.

### Regras de negócio

* Toda exceção conhecida deve retornar JSON padronizado.
* Erros de validação devem informar campo e mensagem.
* Erros inesperados não devem vazar stack trace para o cliente.

### Modelo de erro

```json
{
  "timestamp": "2026-05-10T12:00:00",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Invalid request data",
  "path": "/api/jobs",
  "fields": [
    {
      "name": "type",
      "message": "must not be blank"
    }
  ]
}
```

### Critérios de aceite

* Erro de validação retorna `400`.
* Recurso não encontrado retorna `404`.
* Erro inesperado retorna `500` com mensagem segura.

### Tarefas técnicas

* Criar DTO de erro.
* Criar `GlobalExceptionHandler`.
* Criar exceção base de negócio.
* Testar ao menos um caso de validação.

### O que quero ver no code review

* Erros consistentes.
* Sem `try/catch` espalhado em controller.
* Mensagens úteis, mas sem vazar detalhe interno.

---

# Épico 1 — Engine de jobs assíncronos

## Objetivo

Criar um módulo profissional para controlar processos longos. Esse módulo será útil tanto para o Jarvis quanto para portfólio de Java backend.

---

## JW-010 — Criar entidade Job

### Estória

Como sistema, quero registrar jobs no banco para acompanhar processamentos longos com status, progresso e metadados.

### Regras de negócio

* Todo job deve ter um identificador único.
* Todo job começa com status `PENDING`.
* Todo job deve ter um tipo.
* O progresso inicial deve ser `0`.
* O progresso máximo deve ser `100`.
* O job pode ter metadados em JSON.

### Campos sugeridos

Tabela: `job_execution`

* `id`
* `public_id`
* `type`
* `status`
* `progress`
* `current_step`
* `total_steps`
* `message`
* `metadata_json`
* `created_at`
* `started_at`
* `finished_at`
* `updated_at`
* `error_code`
* `error_message`

### Status permitidos

* `PENDING`
* `RUNNING`
* `COMPLETED`
* `FAILED`
* `CANCELLED`

### Critérios de aceite

* Migration cria tabela `job_execution`.
* Entidade JPA representa a tabela.
* Repository consegue salvar e buscar job.
* Teste de repository valida persistência básica.

### Tarefas técnicas

* Criar enum `JobStatus`.
* Criar enum ou campo controlado para `JobType`.
* Criar entity `JobExecution`.
* Criar repository.
* Criar migration.
* Criar teste com Testcontainers ou teste de repository.

### O que quero ver no code review

* Uso correto de enum.
* Datas bem tratadas.
* `public_id` separado do ID interno.
* Índices para status e tipo.

---

## JW-011 — Criar endpoint para iniciar job

### Estória

Como usuário da API, quero criar um job para iniciar um processamento controlado pelo sistema.

### Endpoint

`POST /api/jobs`

### Request

```json
{
  "type": "TEXT_IMPORT",
  "metadata": {
    "source": "manual-test",
    "description": "Primeiro teste de job"
  }
}
```

### Response

```json
{
  "id": "job_abc123",
  "type": "TEXT_IMPORT",
  "status": "PENDING",
  "progress": 0,
  "message": "Job created successfully"
}
```

### Regras de negócio

* `type` é obrigatório.
* Se o tipo não for suportado, retornar `400`.
* O job deve ser persistido antes de qualquer processamento.
* O endpoint não deve executar trabalho pesado diretamente.

### Critérios de aceite

* Criar job válido retorna `201`.
* Criar job sem tipo retorna `400`.
* Criar job com tipo inválido retorna `400`.
* Job criado aparece no banco com status `PENDING`.

### Tarefas técnicas

* Criar DTO de request.
* Criar DTO de response.
* Criar service `JobService`.
* Criar controller.
* Criar validações.
* Criar testes de service e controller.

### O que quero ver no code review

* Controller fino.
* Service com regra de negócio.
* DTOs separados da entity.
* Validação clara.

---

## JW-012 — Consultar job por ID público

### Estória

Como usuário da API, quero consultar um job pelo ID público para acompanhar o status do processamento.

### Endpoint

`GET /api/jobs/{publicId}`

### Response

```json
{
  "id": "job_abc123",
  "type": "TEXT_IMPORT",
  "status": "RUNNING",
  "progress": 45,
  "currentStep": 3,
  "totalSteps": 7,
  "message": "Processing paragraphs",
  "createdAt": "2026-05-10T12:00:00",
  "startedAt": "2026-05-10T12:00:05",
  "finishedAt": null
}
```

### Regras de negócio

* Buscar por `public_id`, não pelo ID interno.
* Se não encontrar, retornar `404`.
* Não expor campos internos desnecessários.

### Critérios de aceite

* Job existente retorna `200`.
* Job inexistente retorna `404`.
* Response não expõe ID interno do banco.

### Tarefas técnicas

* Criar método no repository.
* Criar método no service.
* Criar endpoint.
* Criar mapper entity → response.
* Testar caso encontrado e não encontrado.

### O que quero ver no code review

* Nada de retornar entity direto.
* Erro 404 padronizado.
* Mapper limpo.

---

## JW-013 — Listar jobs com filtros e paginação

### Estória

Como usuário, quero listar jobs com filtros para encontrar execuções por status, tipo e data.

### Endpoint

`GET /api/jobs?status=RUNNING&type=TEXT_IMPORT&page=0&size=20`

### Regras de negócio

* Paginação obrigatória.
* Tamanho máximo da página: 100.
* Filtros opcionais: status, type, createdAtFrom, createdAtTo.
* Ordenação padrão: mais recentes primeiro.

### Critérios de aceite

* Lista jobs paginados.
* Filtra por status.
* Filtra por tipo.
* Não permite `size` maior que 100.
* Retorna metadados de paginação.

### Tarefas técnicas

* Criar query com Specification ou métodos derivados simples.
* Criar DTO de filtro.
* Criar response paginado.
* Testar paginação e filtros.

### O que quero ver no code review

* Query organizada.
* Sem carregar tudo em memória para filtrar.
* Paginação real no banco.

---

## JW-014 — Atualizar progresso do job

### Estória

Como sistema, quero atualizar o progresso de um job para informar em que etapa o processamento está.

### Regras de negócio

* Só jobs `PENDING` ou `RUNNING` podem receber progresso.
* Ao iniciar o processamento, status muda para `RUNNING`.
* Progresso não pode diminuir sem justificativa técnica.
* Progresso deve ficar entre `0` e `100`.
* Quando progresso chega a `100`, o job ainda não deve virar `COMPLETED` automaticamente; conclusão é uma ação separada.

### Critérios de aceite

* Atualizar progresso válido persiste no banco.
* Progresso menor que 0 retorna erro.
* Progresso maior que 100 retorna erro.
* Job `COMPLETED`, `FAILED` ou `CANCELLED` não pode ser atualizado.

### Tarefas técnicas

* Criar método interno no service.
* Criar validação de status.
* Criar testes unitários.

### O que quero ver no code review

* Regra centralizada no service.
* Mensagens de erro claras.
* Nenhuma atualização inconsistente de status.

---

## JW-015 — Finalizar job com sucesso

### Estória

Como sistema, quero marcar um job como concluído para registrar o fim de um processamento bem-sucedido.

### Regras de negócio

* Apenas job `RUNNING` pode ser concluído.
* Ao concluir, status vira `COMPLETED`.
* `finished_at` deve ser preenchido.
* Progresso deve virar `100`.
* Mensagem final deve ser registrada.

### Critérios de aceite

* Job `RUNNING` pode ser concluído.
* Job já finalizado não pode ser concluído novamente.
* Datas são preenchidas corretamente.

### Tarefas técnicas

* Criar método `completeJob`.
* Criar testes de transição de status.

### O que quero ver no code review

* Máquina de estados respeitada.
* Testes cobrindo transições inválidas.

---

## JW-016 — Finalizar job com erro

### Estória

Como sistema, quero marcar um job como falho para registrar erro e permitir diagnóstico.

### Regras de negócio

* Job `PENDING` ou `RUNNING` pode virar `FAILED`.
* Deve salvar `error_code`.
* Deve salvar `error_message`.
* Deve preencher `finished_at`.
* Não deve salvar stack trace gigante no campo principal.

### Critérios de aceite

* Falha é persistida corretamente.
* Erro aparece na consulta do job.
* Job `COMPLETED` não pode virar `FAILED`.

### Tarefas técnicas

* Criar método `failJob`.
* Criar enum ou constantes de error code.
* Criar testes.

### O que quero ver no code review

* Tratamento seguro de erro.
* Separação entre mensagem para usuário e detalhe técnico.

---

# Épico 2 — Upload e ingestão de texto

## Objetivo

Criar uma funcionalidade simples e útil para importar textos. Antes de processar EPUB real, começar com TXT/Markdown para validar arquitetura.

---

## JW-020 — Upload de arquivo TXT/Markdown

### Estória

Como usuário, quero enviar um arquivo de texto para que o sistema crie uma fonte processável.

### Endpoint

`POST /api/documents/upload`

### Regras de negócio

* Aceitar apenas `.txt` e `.md` no primeiro momento.
* Tamanho máximo inicial: 10 MB.
* O conteúdo deve ser salvo no banco ou em armazenamento local controlado.
* O documento deve ter status `IMPORTED`.
* O sistema deve registrar nome original, tamanho e tipo.

### Critérios de aceite

* Upload válido retorna `201`.
* Arquivo vazio retorna `400`.
* Extensão inválida retorna `400`.
* Arquivo acima do limite retorna `400`.

### Tarefas técnicas

* Criar tabela `document_source`.
* Criar endpoint multipart.
* Criar validação de tipo e tamanho.
* Criar service de armazenamento.
* Criar testes.

### O que quero ver no code review

* Validação antes de persistir.
* Nada de path inseguro.
* Nome original tratado com cuidado.

---

## JW-021 — Listar documentos importados

### Estória

Como usuário, quero listar documentos importados para escolher qual processar.

### Endpoint

`GET /api/documents?page=0&size=20`

### Regras de negócio

* Listagem paginada.
* Ordenar por data de importação desc.
* Não retornar conteúdo completo na listagem.

### Critérios de aceite

* Retorna documentos paginados.
* Não retorna texto inteiro.
* Documento inexistente não quebra listagem.

### Tarefas técnicas

* Criar DTO resumido.
* Criar endpoint.
* Criar teste.

---

## JW-022 — Consultar detalhe de documento

### Estória

Como usuário, quero consultar os detalhes de um documento para verificar metadados e status.

### Endpoint

`GET /api/documents/{id}`

### Regras de negócio

* Buscar por ID público.
* Retornar metadados.
* Retornar preview curto do conteúdo.
* Não retornar conteúdo completo se for muito grande.

### Critérios de aceite

* Documento existente retorna `200`.
* Documento inexistente retorna `404`.
* Preview tem limite de caracteres.

---

# Épico 3 — Segmentação de texto

## Objetivo

Criar processamento de texto em partes menores: parágrafos e sentenças. Isso é útil para Jarvis, NLP e demonstração técnica.

---

## JW-030 — Criar job de segmentação por parágrafos

### Estória

Como usuário, quero iniciar uma segmentação de documento em parágrafos para preparar o texto para processamento posterior.

### Endpoint

`POST /api/documents/{documentId}/segment/paragraphs`

### Regras de negócio

* Deve criar um job do tipo `PARAGRAPH_SEGMENTATION`.
* O processamento deve ser assíncrono.
* Cada parágrafo deve ter índice sequencial.
* Parágrafos vazios devem ser ignorados.
* O texto original não deve ser alterado.

### Critérios de aceite

* Endpoint retorna job criado.
* Job processa documento e gera parágrafos.
* Parágrafos são salvos com ordem correta.
* Job finaliza como `COMPLETED` em caso de sucesso.
* Job finaliza como `FAILED` em caso de erro.

### Tarefas técnicas

* Criar tabela `document_paragraph`.
* Criar service de segmentação.
* Criar executor simples assíncrono.
* Integrar com JobService.
* Criar testes de segmentação.

### O que quero ver no code review

* Algoritmo simples e legível.
* Separação entre orquestração de job e lógica de segmentação.
* Testes com casos de linha vazia, múltiplas quebras e espaços.

---

## JW-031 — Consultar parágrafos de um documento

### Estória

Como usuário, quero consultar os parágrafos segmentados de um documento para validar se a segmentação ficou correta.

### Endpoint

`GET /api/documents/{documentId}/paragraphs?page=0&size=50`

### Regras de negócio

* Retornar parágrafos ordenados pelo índice.
* Paginação obrigatória.
* Não permitir `size` maior que 200.

### Critérios de aceite

* Retorna parágrafos em ordem.
* Documento sem segmentação retorna lista vazia.
* Documento inexistente retorna `404`.

---

## JW-032 — Criar segmentação por sentenças

### Estória

Como usuário, quero segmentar parágrafos em sentenças para preparar alinhamento e análise textual.

### Regras de negócio

* Cada sentença pertence a um parágrafo.
* Cada sentença tem índice global e índice dentro do parágrafo.
* O sistema deve preservar pontuação.
* Não deve quebrar em abreviações comuns quando possível.

### Critérios de aceite

* Sentenças são salvas em ordem.
* Cada sentença referencia o parágrafo de origem.
* Casos básicos com ponto, interrogação e exclamação funcionam.

### Tarefas técnicas

* Criar tabela `document_sentence`.
* Criar sentence splitter inicial.
* Criar testes com exemplos.

### Observação de arquitetura

No início, o splitter pode ser simples. Depois podemos evoluir para ICU4J, OpenNLP, spaCy externo ou outro segmentador.

---

# Épico 4 — Pares bilíngues e alinhamento simples

## Objetivo

Criar a base para alinhar textos EN/PT de forma controlada, começando simples e evoluindo.

---

## JW-040 — Criar entidade DocumentPair

### Estória

Como usuário, quero vincular um documento de origem e um documento de destino para representar um par bilíngue.

### Endpoint

`POST /api/document-pairs`

### Request

```json
{
  "sourceDocumentId": "doc_en_123",
  "targetDocumentId": "doc_pt_456",
  "sourceLang": "en",
  "targetLang": "pt-BR",
  "name": "Livro teste EN/PT"
}
```

### Regras de negócio

* Documento origem e destino devem existir.
* Idiomas são obrigatórios.
* Não pode criar par com o mesmo documento dos dois lados.
* Nome é obrigatório.

### Critérios de aceite

* Par válido retorna `201`.
* Documento inexistente retorna `404`.
* Mesmo documento dos dois lados retorna `400`.

### Tarefas técnicas

* Criar tabela `document_pair`.
* Criar DTOs.
* Criar service.
* Criar endpoint.
* Criar testes.

---

## JW-041 — Alinhamento baseline por índice

### Estória

Como usuário, quero gerar um alinhamento inicial por índice para ter uma baseline simples de comparação.

### Regras de negócio

* O parágrafo 1 da origem é alinhado com o parágrafo 1 do destino.
* O processo continua até acabar um dos lados.
* Pares excedentes devem ser marcados como não alinhados.
* Esse alinhamento deve ser identificado como `INDEX_BASELINE`.

### Critérios de aceite

* Gera pares 1→1 por índice.
* Registra algoritmo usado.
* Registra score padrão ou nulo.
* Mantém rastreabilidade dos parágrafos originais.

### Tarefas técnicas

* Criar tabela `alignment_pair`.
* Criar enum `AlignmentAlgorithm`.
* Criar job de alinhamento baseline.
* Criar endpoint para iniciar alinhamento.
* Criar consulta de pares alinhados.

### O que quero ver no code review

* Rastreabilidade boa.
* Nenhum texto perdido.
* Algoritmo isolado em classe própria.

---

## JW-042 — Calcular heurística de tamanho do par

### Estória

Como sistema, quero calcular uma pontuação simples baseada na diferença de tamanho entre origem e destino para indicar possíveis pares ruins.

### Regras de negócio

* Comparar quantidade de caracteres ou tokens simples.
* Ratio muito baixo ou muito alto deve reduzir score.
* Score deve ficar entre 0 e 1.
* A heurística não decide sozinha se o par é certo; ela apenas ajuda.

### Critérios de aceite

* Pares com tamanhos parecidos recebem score alto.
* Pares muito desbalanceados recebem score baixo.
* Score é persistido no alinhamento.

### Tarefas técnicas

* Criar classe `LengthRatioScorer`.
* Criar testes unitários.
* Integrar ao alinhamento baseline.

---

# Épico 5 — Memória RAG básica

## Objetivo

Criar uma memória textual pesquisável, primeiro com busca textual simples e depois com embeddings.

---

## JW-050 — Aprovar par alinhado para memória

### Estória

Como usuário, quero aprovar um par alinhado para que ele entre na memória canônica do sistema.

### Regras de negócio

* Apenas pares existentes podem ser aprovados.
* Um par aprovado gera registro em `memory_pair`.
* O sistema deve preservar origem, destino, algoritmo e scores.
* Não deve duplicar o mesmo par aprovado.

### Critérios de aceite

* Aprovar par cria memória.
* Aprovar o mesmo par duas vezes não duplica.
* Memória mantém referência ao alinhamento original.

### Tarefas técnicas

* Criar tabela `memory_pair`.
* Criar endpoint de aprovação.
* Criar service.
* Criar testes.

---

## JW-051 — Buscar memória por texto simples

### Estória

Como usuário, quero buscar pares aprovados por texto para reutilizar exemplos de tradução.

### Endpoint

`GET /api/memory/search?q=dragon`

### Regras de negócio

* Buscar em texto de origem e destino.
* Retornar no máximo 20 resultados inicialmente.
* Ordenar por relevância simples ou data.

### Critérios de aceite

* Busca encontra pares contendo termo.
* Busca vazia retorna erro `400`.
* Resultado inclui origem, destino e metadados.

---

## JW-052 — Preparar estrutura para embeddings

### Estória

Como sistema, quero preparar a estrutura de embeddings para permitir busca semântica no futuro.

### Regras de negócio

* Um par de memória pode ter múltiplos embeddings.
* Embeddings podem ser de origem, destino ou par combinado.
* Deve registrar modelo usado.
* Deve registrar dimensão.

### Tabela sugerida

`memory_embedding`

Campos:

* `id`
* `memory_pair_id`
* `embedding_type`
* `model_name`
* `dimension`
* `vector`
* `created_at`

### Critérios de aceite

* Migration criada.
* Entidade criada.
* Ainda não precisa gerar embedding de verdade.

---

# Épico 6 — Integração com IA local

## Objetivo

Conectar o backend a um modelo local via HTTP, sem depender de API paga.

---

## JW-060 — Criar client HTTP para modelo local

### Estória

Como sistema, quero chamar um modelo local via HTTP para gerar respostas a partir de prompts.

### Regras de negócio

* URL do modelo deve vir de configuração.
* Timeout deve ser configurável.
* Erros de conexão devem ser tratados.
* O client não deve estar acoplado a controller.

### Critérios de aceite

* Service consegue enviar prompt e receber resposta.
* Erro de modelo fora do ar retorna erro controlado.
* Existe teste mockando o client.

### Tarefas técnicas

* Criar interface `LocalModelClient`.
* Criar implementação HTTP.
* Criar DTO de request/response.
* Criar configuração.
* Criar teste.

---

## JW-061 — Criar endpoint de teste de prompt

### Estória

Como desenvolvedor, quero testar prompts pelo backend para validar a integração com IA local.

### Endpoint

`POST /api/ai/prompt-test`

### Request

```json
{
  "prompt": "Traduza a frase para português brasileiro: The dragon landed on the tower."
}
```

### Regras de negócio

* Endpoint deve ser marcado como experimental.
* Prompt não pode ser vazio.
* Deve haver limite de tamanho.
* Deve registrar tempo de resposta.

### Critérios de aceite

* Prompt válido retorna resposta.
* Prompt vazio retorna `400`.
* Modelo fora do ar retorna erro padronizado.

---

# Épico 7 — Frontend profissional

## Objetivo

Criar uma interface simples e bonita para demonstrar o backend como produto real.

---

## JW-070 — Criar aplicação React base

### Estória

Como usuário, quero acessar uma interface web do Jarvis Workbench para usar o sistema sem depender apenas de Postman.

### Regras de negócio

* Deve ter layout base.
* Deve ter navegação entre páginas.
* Deve consumir health check do backend.

### Páginas iniciais

* Dashboard
* Jobs
* Documents
* Memory
* AI Test

### Critérios de aceite

* Aplicação sobe localmente.
* Dashboard mostra status da API.
* Navegação funciona.

---

## JW-071 — Tela de listagem de jobs

### Estória

Como usuário, quero visualizar os jobs em uma tabela para acompanhar os processamentos.

### Regras de negócio

* Deve mostrar status, tipo, progresso e data.
* Deve ter filtros básicos.
* Deve ter paginação.
* Status deve ser visualmente fácil de identificar.

### Critérios de aceite

* Lista jobs do backend.
* Filtra por status.
* Paginação funciona.
* Ao clicar em um job, abre detalhe.

---

## JW-072 — Tela de detalhe do job

### Estória

Como usuário, quero ver os detalhes de um job para entender o que aconteceu durante o processamento.

### Regras de negócio

* Mostrar status atual.
* Mostrar progresso.
* Mostrar mensagem.
* Mostrar erro, se houver.
* Atualizar dados manualmente com botão de refresh.

### Critérios de aceite

* Detalhe carrega por ID.
* Job inexistente mostra erro amigável.
* Erro do backend aparece de forma compreensível.

---

# Épico 8 — Qualidade, documentação e empregabilidade

## Objetivo

Transformar o projeto em algo apresentável para recrutador, entrevista e GitHub.

---

## JW-080 — Criar README profissional

### Estória

Como visitante do GitHub, quero entender rapidamente o que o projeto faz, como rodar e quais tecnologias usa.

### README deve conter

* Nome do projeto.
* Descrição.
* Problema que resolve.
* Arquitetura.
* Stack.
* Como rodar backend.
* Como rodar frontend.
* Como configurar banco.
* Exemplos de endpoints.
* Prints ou GIFs, quando houver.
* Roadmap.

### Critérios de aceite

* Uma pessoa consegue rodar o projeto seguindo o README.
* O README explica por que o projeto é relevante.
* Tem exemplos de request/response.

---

## JW-081 — Criar coleção Postman ou arquivo HTTP

### Estória

Como desenvolvedor ou avaliador técnico, quero testar a API facilmente sem precisar montar requests do zero.

### Critérios de aceite

* Existe coleção Postman ou arquivo `.http`.
* Inclui health, jobs, documents, segmentation e AI test.
* Variáveis de ambiente estão documentadas.

---

## JW-082 — Criar testes principais de integração

### Estória

Como desenvolvedor, quero garantir que os principais fluxos funcionam de ponta a ponta.

### Fluxos mínimos

* Criar job.
* Consultar job.
* Upload de documento.
* Segmentar documento.
* Listar parágrafos.
* Criar par bilíngue.
* Aprovar memória.

### Critérios de aceite

* Testes rodam localmente.
* Testes não dependem de banco manual externo.
* Pipeline básico documentado.

---
