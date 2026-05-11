# Backlog de Desenvolvimento — Jarvis Rebuild Mobile-First

## Direção corrigida

Este backlog substitui o plano genérico anterior.

A meta **não** é criar um CRUD bonito para portfólio. A meta é recriar o **Jarvis de verdade**, com o que ele já faz hoje, só que de forma mais organizada, testável, mobile-first e profissional.

O projeto deve servir para três objetivos ao mesmo tempo:

1. **Jarvis real**: importar EPUB EN/PT, mapear capítulos, alinhar parágrafos/sentenças, validar pares, consolidar memória e preparar tradução.
2. **Aprendizado forte**: desenvolver na mão, entendendo Java backend, arquitetura, banco, jobs, frontend e IA local.
3. **Empregabilidade**: virar um projeto de GitHub que prove domínio de Java/Spring, PostgreSQL, processamento assíncrono, dados, testes, frontend e IA aplicada.

---

# Visão do produto

## Nome do projeto

**Jarvis Workbench**

## Descrição real

Sistema local, mobile-first, para importar pares de livros EPUB em inglês e português brasileiro, extrair estrutura textual, mapear capítulos, alinhar parágrafos/sentenças, validar qualidade dos pares, consolidar memória bilíngue e apoiar tradução literária assistida por IA local.

## Frase de currículo

Desenvolvi uma plataforma local em Java/Spring Boot e React para ingestão e alinhamento de livros EPUB EN/PT-BR, com processamento assíncrono, PostgreSQL, jobs rastreáveis, validação de pares bilíngues, embeddings multilíngues, memória RAG e interface mobile-first.

---

# Princípios arquiteturais

## 1. Backend Java é o coração

O sistema principal deve ser Java/Spring Boot.

Python pode existir depois como apoio para tarefas específicas, mas o produto principal deve provar força em Java.

## 2. Mobile-first desde a primeira tela

A UI deve nascer pensando em celular:

- telas estreitas primeiro;
- cards em vez de tabelas gigantes;
- ações grandes e fáceis de tocar;
- progresso claro dos jobs;
- leitura confortável de pares EN/PT;
- modo revisão adaptado ao celular.

Depois adaptamos para desktop.

## 3. Jobs longos são parte central

Importar EPUB, segmentar, gerar embeddings e alinhar são processos longos.

Então job engine não é genérico: é necessidade real do Jarvis.

## 4. Nada de toy project

Desde cedo o sistema deve trabalhar com EPUB real, capítulos reais, parágrafos reais e alinhamento real.

## 5. Desenvolvimento por estórias pequenas

Mesmo sendo um projeto grande, cada entrega deve ser pequena, testável e revisável.

---

# Stack recomendada

## Backend

- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- Spring Validation
- PostgreSQL
- Flyway
- JUnit 5
- Mockito
- Testcontainers
- OpenAPI/Swagger em fase posterior

## EPUB / texto

Começar com solução Java.

Possíveis caminhos:

- biblioteca EPUB Java para leitura inicial;
- Jsoup para limpar XHTML interno;
- fallback manual lendo estrutura ZIP/OPF se necessário;
- ICU4J ou BreakIterator em fase posterior para segmentação melhor.

## Frontend

- React
- TypeScript
- Tailwind CSS
- Shadcn/ui
- React Hook Form
- Zod
- TanStack Query
- Mobile-first

## IA / embeddings

- LaBSE ONNX Java, se mantivermos caminho atual;
- Ollama para LLM local;
- embeddings locais em fase posterior;
- pgvector em fase posterior.

---

# Estrutura de módulos backend

Estrutura alvo:

```text
src/main/java/com/dnobretech/jarvisworkbench/

  health/
    api/
    dto/

  job/
    api/
    application/
    domain/
    repository/
    dto/

  epub/
    api/
    application/
    domain/
    repository/
    dto/
    parser/

  book/
    api/
    application/
    domain/
    repository/
    dto/

  text/
    segmentation/
    normalization/
    similarity/

  alignment/
    api/
    application/
    domain/
    repository/
    dto/
    algorithm/
    scoring/

  embedding/
    application/
    provider/
    dto/

  memory/
    api/
    application/
    domain/
    repository/
    dto/

  translation/
    api/
    application/
    dto/

  shared/
    error/
    config/
    pagination/
    time/
```

---

# Modelo de execução revisado

## Fase 0 — Fundação

Criar base do projeto com health check, banco, migrations e erro padronizado.

## Fase 1 — Job engine real

Criar engine de jobs porque tudo no Jarvis depende disso.

## Fase 2 — Núcleo EPUB

Importar EPUB real, extrair estrutura, capítulos e parágrafos.

## Fase 3 — Par de livros EN/PT

Criar entidade para par de livros, importar dois EPUBs e organizar capítulos/parágrafos dos dois lados.

## Fase 4 — Macro alinhamento

Recriar o alinhamento maneiro atual: janela de parágrafos, candidatos, score, skip, merge e rastreabilidade.

## Fase 5 — Revisão mobile-first

Criar telas para revisar importação, capítulos, parágrafos e pares alinhados no celular.

## Fase 6 — Alinhamento fino/sentenças

Quebrar pares aprovados em sentenças e alinhar com mais precisão.

## Fase 7 — Validação e qualidade

Criar métricas, flags, score final e revisão assistida.

## Fase 8 — Memória canônica/RAG

Consolidar pares aprovados em memória pesquisável.

## Fase 9 — Tradução assistida

Usar memória, glossário e IA local para sugerir tradução e revisão.

---

# Épico 0 — Fundação do projeto

## JW-001 — Health check e base Spring Boot

Status: **DONE**

### Objetivo

Criar projeto Spring Boot base, endpoint de saúde e README inicial.

---

## JW-002 — Configurar PostgreSQL e Flyway para o Jarvis real

### Estória

Como desenvolvedor, quero configurar PostgreSQL e Flyway para que o Jarvis tenha um banco versionado e pronto para armazenar livros, EPUBs, capítulos, parágrafos, jobs e alinhamentos.

### Regras de negócio

- Toda tabela deve nascer via migration.
- O backend não deve depender de `ddl-auto=create`.
- A aplicação deve usar PostgreSQL local.
- Configurações sensíveis devem ficar fora do código.
- A migration inicial deve preparar a base para o Jarvis, não uma tabela de teste sem sentido.

### Escopo da JW-002
JW-002 — DONE

Criar apenas a infraestrutura de banco e uma tabela simples de controle da aplicação.

Ainda não criaremos tabelas de EPUB, jobs ou alinhamento nesta estória.

### Dependências Maven esperadas

- `spring-boot-starter-data-jpa`
- `postgresql`
- `flyway-core`
- `flyway-database-postgresql`, se necessário na versão usada

### Arquivos esperados

```text
src/main/resources/application.yml
src/main/resources/application-local.yml
src/main/resources/db/migration/V1__init_schema.sql
```

### Configuração esperada

- Profile local configurável.
- Datasource apontando para PostgreSQL local.
- JPA sem criar tabela automaticamente.
- Flyway habilitado.

### Critérios de aceite

- Aplicação sobe conectando no PostgreSQL.
- Flyway executa `V1__init_schema.sql`.
- Tabela `app_info` ou equivalente é criada.
- `ddl-auto` não está como `create` ou `update`.
- README explica como criar banco local.
- Health check continua funcionando.
- Testes existentes continuam passando.

### O que quero ver no code review

- Configuração limpa.
- Sem senha hardcoded de forma perigosa.
- Migration organizada.
- README claro para rodar no Windows.

---

## JW-003 — Erro padronizado da API

### Estória

Como usuário da API, quero receber erros padronizados para entender falhas de validação, recurso inexistente e erros internos.

### Regras de negócio

- Erros devem retornar JSON consistente.
- Validação deve mostrar campos inválidos.
- Erros inesperados não devem vazar stack trace.
- O formato deve servir para backend e frontend mobile.

### Modelo esperado

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

### Critérios de aceite

- Erro de validação retorna 400.
- Recurso não encontrado retorna 404.
- Erro de negócio retorna 422 ou 400, conforme caso.
- Erro interno retorna 500 seguro.

---

# Épico 1 — Job engine real do Jarvis

## JW-010 — Criar entidade JobExecution

### Estória

Como sistema, quero registrar processamentos longos para acompanhar importação EPUB, segmentação, alinhamento e tradução.

### Tipos iniciais de job

- `EPUB_IMPORT`
- `BOOK_PAIR_IMPORT`
- `CHAPTER_MAPPING`
- `PARAGRAPH_ALIGNMENT`
- `SENTENCE_ALIGNMENT`
- `MEMORY_BUILD`
- `TRANSLATION_RUN`

### Status

- `PENDING`
- `RUNNING`
- `COMPLETED`
- `FAILED`
- `CANCELLED`

### Campos sugeridos

Tabela: `job_execution`

- `id`
- `public_id`
- `type`
- `status`
- `progress`
- `current_step`
- `total_steps`
- `message`
- `metadata_json`
- `created_at`
- `started_at`
- `finished_at`
- `updated_at`
- `error_code`
- `error_message`

### Critérios de aceite

- Migration cria tabela.
- Entity JPA criada.
- Repository criado.
- Job novo começa como `PENDING` e progresso `0`.
- Teste de persistência básico.

---

## JW-011 — Criar, consultar e listar jobs

### Estória

Como usuário, quero criar e consultar jobs para acompanhar processos do Jarvis pelo celular.

### Endpoints

```http
POST /api/jobs
GET /api/jobs/{publicId}
GET /api/jobs?status=RUNNING&type=EPUB_IMPORT&page=0&size=20
```

### Regras de negócio

- Criar job não deve executar processamento pesado diretamente.
- Listagem deve ser paginada.
- Tamanho máximo da página: 100.
- Resposta deve ser amigável para cards mobile.

### Critérios de aceite

- Cria job válido.
- Consulta por ID público.
- Lista com filtros.
- Não expõe ID interno do banco.

---

## JW-012 — Ciclo de vida do job

### Estória

Como sistema, quero iniciar, atualizar progresso, concluir e falhar jobs com regras consistentes.

### Regras de negócio

- `PENDING` pode virar `RUNNING`.
- `RUNNING` pode virar `COMPLETED`, `FAILED` ou `CANCELLED`.
- `COMPLETED` não volta para outro status.
- `FAILED` não vira `COMPLETED` sem novo job.
- Progresso deve ficar entre 0 e 100.
- `finished_at` deve ser preenchido ao finalizar.

### Critérios de aceite

- Transições válidas funcionam.
- Transições inválidas falham.
- Testes cobrem regras principais.

---

## JW-013 — Log de eventos do job

### Estória

Como usuário, quero ver eventos de um job para entender onde a importação ou alinhamento falhou.

### Tabela sugerida

`job_event`

Campos:

- `id`
- `job_execution_id`
- `level`
- `message`
- `details_json`
- `created_at`

### Níveis

- `INFO`
- `WARN`
- `ERROR`
- `DEBUG`

### Critérios de aceite

- Job pode registrar eventos.
- Endpoint retorna timeline do job.
- Frontend mobile consegue exibir os eventos como lista.

---

# Épico 2 — Importação EPUB real

## JW-020 — Upload de EPUB individual

### Estória

Como usuário, quero enviar um arquivo EPUB para o Jarvis para que ele extraia metadados, capítulos e parágrafos.

### Endpoint

```http
POST /api/epubs/upload
```

### Regras de negócio

- Aceitar apenas `.epub`.
- Tamanho máximo configurável.
- Salvar nome original, tamanho, hash e data de upload.
- Não processar pesado dentro do request.
- Criar job `EPUB_IMPORT`.
- Preservar arquivo original em storage local ou banco, conforme decisão arquitetural.

### Critérios de aceite

- Upload válido cria registro e job.
- Arquivo inválido retorna erro padronizado.
- Arquivo vazio retorna erro.
- Hash do arquivo é calculado.
- README explica como testar com Insomnia/Postman.

---

## JW-021 — Extrair estrutura básica do EPUB

### Estória

Como sistema, quero extrair a estrutura do EPUB para identificar título, autor, arquivos internos e ordem de leitura.

### Regras de negócio

- O EPUB deve ser tratado como pacote estruturado.
- O sistema deve identificar spine/ordem de leitura quando possível.
- Arquivos XHTML/HTML devem ser lidos em ordem.
- Conteúdo bruto deve ser preservado para diagnóstico.

### Saídas esperadas

- título, se existir;
- autor, se existir;
- idioma, se existir;
- lista ordenada de documentos internos;
- status da extração.

### Critérios de aceite

- EPUB real é lido sem erro.
- Ordem de leitura é respeitada quando disponível.
- Logs mostram quantos itens foram extraídos.

---

## JW-022 — Extrair capítulos do EPUB

### Estória

Como sistema, quero converter a estrutura interna do EPUB em capítulos processáveis.

### Regras de negócio

- Cada capítulo deve ter índice sequencial.
- Cada capítulo deve ter título detectado quando possível.
- Capítulos sem título devem receber label técnico.
- XHTML deve ser limpo para texto legível.
- Itálicos e marcações importantes podem virar metadados em fase posterior.

### Tabela sugerida

`epub_chapter`

Campos:

- `id`
- `epub_id`
- `chapter_index`
- `title`
- `source_href`
- `raw_html`
- `plain_text`
- `char_count`
- `created_at`

### Critérios de aceite

- Capítulos são salvos em ordem.
- Texto limpo é gerado.
- O sistema registra quantidade de caracteres.
- Endpoint permite listar capítulos do EPUB.

---

## JW-023 — Extrair parágrafos dos capítulos

### Estória

Como sistema, quero extrair parágrafos dos capítulos para preparar alinhamento.

### Regras de negócio

- Parágrafos vazios devem ser ignorados.
- Parágrafos devem manter ordem global e ordem dentro do capítulo.
- Parágrafos devem guardar referência ao capítulo.
- Texto deve ser normalizado sem destruir pontuação.
- Parágrafo muito pequeno pode ser marcado com flag, mas não descartado automaticamente.

### Tabela sugerida

`epub_paragraph`

Campos:

- `id`
- `epub_id`
- `chapter_id`
- `global_index`
- `chapter_index`
- `paragraph_index`
- `text`
- `char_count`
- `token_count_estimate`
- `flags_json`

### Critérios de aceite

- Parágrafos são salvos em ordem.
- Endpoint lista parágrafos por EPUB ou capítulo.
- Casos com múltiplos `<p>` funcionam.
- Parágrafos com espaços e quebras são normalizados.

---

## JW-024 — Tela mobile de EPUB importado

### Estória

Como usuário no celular, quero ver os EPUBs importados e abrir detalhes de capítulos/parágrafos.

### Regras de UX mobile

- Lista em cards.
- Mostrar título, idioma, quantidade de capítulos, quantidade de parágrafos e status.
- Ações grandes: abrir, excluir, reprocessar.
- Tela de detalhe deve ter abas ou seções recolhíveis.

### Critérios de aceite

- Tela funciona bem em largura de celular.
- Não usa tabela larga.
- Usuário consegue abrir capítulos e preview de parágrafos.

---

# Épico 3 — Par de livros EN/PT

## JW-030 — Criar BookPair a partir de dois EPUBs

### Estória

Como usuário, quero vincular um EPUB em inglês e um EPUB em português para criar um par bilíngue processável.

### Endpoint

```http
POST /api/book-pairs
```

### Request

```json
{
  "name": "Livro Teste EN/PT",
  "sourceEpubId": "epub_en_123",
  "targetEpubId": "epub_pt_456",
  "sourceLang": "en",
  "targetLang": "pt-BR"
}
```

### Regras de negócio

- Os dois EPUBs devem existir.
- Não pode usar o mesmo EPUB dos dois lados.
- Idiomas são obrigatórios.
- Par deve ter status inicial `CREATED`.

### Critérios de aceite

- Par válido é criado.
- Par inválido retorna erro.
- Tela mobile lista pares de livros.

---

## JW-031 — Dashboard mobile do BookPair

### Estória

Como usuário no celular, quero ver o estado geral de um par de livros antes de alinhar.

### Informações exibidas

- nome do par;
- EPUB origem e destino;
- idiomas;
- capítulos EN/PT;
- parágrafos EN/PT;
- último job executado;
- próximos passos recomendados.

### Critérios de aceite

- Tela é legível no celular.
- Usuário entende se já pode iniciar mapeamento/alinhamento.

---

# Épico 4 — Mapeamento de capítulos

## JW-040 — Mapear capítulos por índice baseline

### Estória

Como sistema, quero gerar um mapeamento inicial de capítulos por índice para ter uma baseline simples.

### Regras de negócio

- Capítulo EN 1 tenta mapear com PT 1.
- Excedentes ficam como não mapeados.
- Algoritmo usado deve ser registrado como `CHAPTER_INDEX_BASELINE`.

### Critérios de aceite

- Gera chapter map inicial.
- Preserva rastreabilidade.
- Pode ser visualizado no mobile.

---

## JW-041 — Mapear capítulos com embeddings/window

### Estória

Como sistema, quero mapear capítulos usando similaridade semântica e janela para lidar com offset entre versões EN/PT.

### Regras de negócio

- Para cada capítulo EN, buscar candidatos PT em janela próxima.
- Calcular score semântico.
- Considerar diferença de tamanho.
- Permitir offset global.
- Registrar algoritmo como `CHAPTER_EMBEDDING_WINDOW`.

### Parâmetros iniciais

- `windowK`
- `expandStep`
- `maxExpansions`
- `maxCharsForEmbedding`

### Critérios de aceite

- Gera mapa melhor que baseline em casos com deslocamento.
- Score é salvo.
- Capítulos suspeitos ficam marcados.

---

## JW-042 — Tela mobile de revisão do mapa de capítulos

### Estória

Como usuário, quero revisar o mapeamento de capítulos no celular antes de alinhar parágrafos.

### Regras de UX

- Mostrar card EN/PT lado a lado ou empilhado.
- Mostrar score e flags.
- Permitir aprovar, rejeitar ou ajustar par.
- Destacar capítulos sem par.

### Critérios de aceite

- Usuário consegue revisar pelo celular.
- Pares suspeitos são fáceis de identificar.

---

# Épico 5 — Alinhamento de parágrafos real

## JW-050 — Criar estrutura de AlignmentRun

### Estória

Como sistema, quero registrar uma execução de alinhamento para comparar algoritmos, parâmetros e resultados.

### Tabelas sugeridas

`alignment_run`

- `id`
- `public_id`
- `book_pair_id`
- `algorithm`
- `status`
- `parameters_json`
- `started_at`
- `finished_at`
- `summary_json`

`paragraph_alignment_pair`

- `id`
- `alignment_run_id`
- `src_start_index`
- `src_end_index`
- `tgt_start_index`
- `tgt_end_index`
- `src_text`
- `tgt_text`
- `alignment_type`
- `score`
- `semantic_score`
- `length_score`
- `flags_json`

### Critérios de aceite

- AlignmentRun é criado.
- Pares alinhados são persistidos.
- Resultados são rastreáveis por run.

---

## JW-051 — Alinhamento baseline por índice de parágrafos

### Estória

Como sistema, quero gerar baseline por índice para comparar com algoritmos melhores.

### Regras de negócio

- Parágrafo EN N com PT N.
- Excedentes viram `SKIP_SRC` ou `SKIP_TGT`.
- Score inicial baseado em tamanho.

### Critérios de aceite

- Baseline executa como job.
- Resultados são consultáveis.
- Serve como comparação.

---

## JW-052 — Alinhamento por janela de parágrafos

### Estória

Como sistema, quero alinhar parágrafos usando janela de candidatos, permitindo 1→1, 1→2, 2→1 e skips.

### Regras de negócio

- Para cada parágrafo origem, avaliar candidatos próximos no destino.
- Gerar candidatos 1→1, 1→2 e 2→1 quando fizer sentido.
- Calcular score composto.
- Se score baixo, tentar avançar no destino.
- Não permitir cruzamento de ordem.
- Registrar tipo do alinhamento.

### Tipos

- `ONE_TO_ONE`
- `ONE_TO_MANY`
- `MANY_TO_ONE`
- `SKIP_SRC`
- `SKIP_TGT`
- `UNCERTAIN`

### Critérios de aceite

- Algoritmo funciona em livro com desbalanceamento de parágrafos.
- Pares 1→2 e 2→1 aparecem corretamente.
- Skips são registrados.
- Score e flags são salvos.

---

## JW-053 — Score composto de alinhamento

### Estória

Como sistema, quero calcular um score composto para avaliar qualidade do par alinhado.

### Componentes iniciais

- score semântico;
- score de tamanho;
- penalidade por texto muito curto;
- penalidade por divergência extrema;
- bônus por termos-chave compartilhados, em fase posterior.

### Critérios de aceite

- Score final fica entre 0 e 1.
- Pares bons tendem a score alto.
- Pares ruins tendem a score baixo.
- Testes unitários cobrem cenários simples.

---

## JW-054 — Tela mobile de revisão de alinhamento

### Estória

Como usuário, quero revisar pares alinhados no celular para aprovar, rejeitar ou marcar como suspeito.

### Regras de UX

- Card com texto EN e PT empilhados.
- Mostrar score, tipo e flags.
- Botões grandes: aprovar, rejeitar, editar, próximo.
- Filtro por `suspeitos`, `score baixo`, `tipo`, `capítulo`.
- Não usar tabela larga.

### Critérios de aceite

- Revisão é confortável no celular.
- Usuário consegue aprovar/rejeitar rapidamente.
- Pares suspeitos são fáceis de encontrar.

---

# Épico 6 — Segmentação e alinhamento de sentenças

## JW-060 — Segmentar parágrafos aprovados em sentenças

### Estória

Como sistema, quero quebrar pares de parágrafos aprovados em sentenças para gerar pares menores e melhores para RAG/tradução.

### Regras de negócio

- Cada sentença mantém referência ao parágrafo.
- Ordem é preservada.
- Pontuação é preservada.
- Abreviações comuns não devem quebrar de forma grosseira, quando possível.

### Critérios de aceite

- Sentenças são salvas.
- Casos simples funcionam.
- Erros ficam rastreáveis.

---

## JW-061 — Alinhar sentenças dentro de pares aprovados

### Estória

Como sistema, quero alinhar sentenças dentro de um par de parágrafos para criar memória mais precisa.

### Regras de negócio

- Alinhamento ocorre dentro do parágrafo já aprovado.
- Permitir 1→1, 1→2, 2→1 e skip.
- Score próprio de sentença.
- Resultado não substitui automaticamente o parágrafo; complementa.

### Critérios de aceite

- Sentenças alinhadas são persistidas.
- Pares suspeitos são marcados.
- Tela permite revisar depois.

---

# Épico 7 — Validação e qualidade

## JW-070 — Flags de qualidade

### Estória

Como sistema, quero marcar problemas prováveis nos pares alinhados para priorizar revisão humana.

### Flags iniciais

- `LOW_SCORE`
- `LENGTH_DIVERGENCE`
- `VERY_SHORT_SOURCE`
- `VERY_SHORT_TARGET`
- `POSSIBLE_MISSING_TEXT`
- `POSSIBLE_EXTRA_TEXT`
- `NUMBER_MISMATCH`
- `QUOTE_MISMATCH`
- `PLACEHOLDER_MISMATCH`

### Critérios de aceite

- Flags são geradas automaticamente.
- Filtros podem usar flags.
- Flags aparecem na UI.

---

## JW-071 — Relatório de qualidade da run

### Estória

Como usuário, quero ver um resumo da qualidade de uma execução de alinhamento.

### Métricas

- total de pares;
- aprovados;
- suspeitos;
- rejeitados;
- score médio;
- quantidade por tipo;
- capítulos com mais problemas;
- skips.

### Critérios de aceite

- Relatório é gerado por run.
- UI mobile mostra resumo em cards.

---

# Épico 8 — Memória canônica/RAG

## JW-080 — Aprovar par para memória canônica

### Estória

Como usuário, quero aprovar pares bons para consolidar uma memória bilíngue confiável.

### Regras de negócio

- Pares aprovados entram em `memory_pair`.
- Deve preservar origem e provenance.
- Não duplicar par idêntico.
- Aceitar nível `PARAGRAPH` e `SENTENCE`.

### Critérios de aceite

- Aprovação cria memória.
- Duplicata é evitada.
- Origem do par continua rastreável.

---

## JW-081 — Busca textual na memória

### Estória

Como usuário, quero buscar exemplos na memória por texto para revisar traduções e termos.

### Critérios de aceite

- Busca em EN e PT.
- Retorna pares relevantes.
- UI mobile exibe cards de memória.

---

## JW-082 — Embeddings e busca semântica

### Estória

Como sistema, quero gerar embeddings de pares aprovados para recuperar exemplos semanticamente semelhantes.

### Regras de negócio

- Registrar modelo usado.
- Registrar dimensão.
- Permitir embeddings de origem, destino e par combinado.
- Busca deve retornar score.

### Critérios de aceite

- Embeddings são gerados.
- Busca semântica funciona.
- Resultados aparecem na UI.

---

# Épico 9 — Tradução assistida

## JW-090 — Criar TranslationRun

### Estória

Como usuário, quero iniciar uma execução de tradução assistida para um trecho/capítulo usando memória e IA local.

### Regras de negócio

- TranslationRun deve registrar modelo, parâmetros, trecho de origem e contexto usado.
- Não sobrescrever tradução humana automaticamente.
- Toda saída da IA deve ser revisável.

### Critérios de aceite

- Run é criada.
- Prompt/contexto são registrados.
- Resultado é consultável.

---

## JW-091 — Montar contexto RAG para tradução

### Estória

Como sistema, quero recuperar exemplos da memória para ajudar o modelo local a traduzir com consistência.

### Regras de negócio

- Buscar exemplos semelhantes.
- Incluir glossário quando existir.
- Limitar tamanho do contexto.
- Registrar quais memórias foram usadas.

### Critérios de aceite

- Contexto é gerado.
- Memórias usadas são rastreáveis.
- Usuário consegue ver o que influenciou a tradução.

---

## JW-092 — Tela mobile de tradução/revisão

### Estória

Como usuário, quero revisar tradução no celular, comparando original, sugestão da IA e memórias recuperadas.

### UX mobile

- Original em card.
- Tradução sugerida em card editável.
- Memórias recuperadas em acordeão.
- Botões: aprovar, editar, rejeitar, próxima.

### Critérios de aceite

- Fluxo de revisão funciona no celular.
- Tradução aprovada pode virar memória.

---

# Épico 10 — Frontend mobile-first desde cedo

## JW-100 — Criar app React mobile-first

### Estória

Como usuário, quero acessar o Jarvis pelo celular com uma interface simples e funcional.

### Páginas iniciais

- Home/Dashboard
- Jobs
- EPUBs
- BookPairs
- Alignment Runs
- Memory

### Critérios de aceite

- App funciona em largura mobile.
- Navegação simples.
- Consome health check.
- Layout não depende de tabela desktop.

---

## JW-101 — Componente padrão de card de status

### Estória

Como usuário mobile, quero visualizar status de jobs, EPUBs e runs em cards consistentes.

### Critérios de aceite

- Card reutilizável.
- Mostra título, subtítulo, status, progresso e ações.
- Funciona bem no celular.

---

## JW-102 — Design system mínimo do Jarvis

### Estória

Como desenvolvedor, quero criar padrões mínimos de UI para não cada tela nascer de um jeito.

### Itens

- botões;
- cards;
- badges de status;
- layout de página;
- loading;
- empty state;
- error state.

### Critérios de aceite

- Componentes básicos criados.
- Telas usam o mesmo padrão.

---

# Ordem recomendada revisada

## Sprint 0 — Base

1. JW-001 — Health check e base Spring Boot. **DONE**
2. JW-002 — PostgreSQL e Flyway.
3. JW-003 — Erro padronizado.

## Sprint 1 — Job engine

1. JW-010 — Entity JobExecution.
2. JW-011 — Criar/consultar/listar jobs.
3. JW-012 — Ciclo de vida de jobs.
4. JW-013 — Log de eventos do job.

## Sprint 2 — EPUB real

1. JW-020 — Upload de EPUB individual.
2. JW-021 — Extrair estrutura básica.
3. JW-022 — Extrair capítulos.
4. JW-023 — Extrair parágrafos.
5. JW-024 — Tela mobile de EPUB importado.

## Sprint 3 — BookPair

1. JW-030 — Criar BookPair EN/PT.
2. JW-031 — Dashboard mobile do BookPair.

## Sprint 4 — Capítulos

1. JW-040 — Mapa baseline por índice.
2. JW-041 — Mapa por embeddings/window.
3. JW-042 — Tela mobile de revisão.

## Sprint 5 — Alinhamento de parágrafos

1. JW-050 — AlignmentRun.
2. JW-051 — Baseline por índice.
3. JW-052 — Window alignment real.
4. JW-053 — Score composto.
5. JW-054 — Tela mobile de revisão.

## Sprint 6 — Sentenças

1. JW-060 — Segmentar sentenças.
2. JW-061 — Alinhar sentenças.

## Sprint 7 — Qualidade

1. JW-070 — Flags.
2. JW-071 — Relatório da run.

## Sprint 8 — Memória

1. JW-080 — Aprovar memória.
2. JW-081 — Busca textual.
3. JW-082 — Busca semântica.

## Sprint 9 — Tradução

1. JW-090 — TranslationRun.
2. JW-091 — Contexto RAG.
3. JW-092 — Tela mobile de tradução.

## Sprint 10 — Frontend consolidado

1. JW-100 — App React mobile-first.
2. JW-101 — Card de status.
3. JW-102 — Design system mínimo.

---

# Como vamos trabalhar daqui para frente

## Você implementa na mão

Eu não vou assumir que você quer código pronto. O padrão será:

1. Eu detalho a estória.
2. Você implementa.
3. Você cola dúvidas ou arquivos.
4. Eu faço code review como LT/arquiteto.
5. Só avançamos quando estiver bom.

## Quando pedir detalhamento

Use:

```text
Chefe, vamos detalhar a JW-002 antes de codar.
Me diga arquivos, dependências, decisões, regras e critérios de aceite.
```

## Quando pedir review

Use:

```text
Chefe, faz code review da JW-002.
Arquivos alterados:
...
```

---

# Decisão importante

O Jarvis não será tratado como projeto paralelo bagunçado.

Ele será tratado como produto técnico real:

- backend forte;
- frontend mobile-first;
- importação EPUB real;
- alinhamento bilíngue real;
- IA local como diferencial;
- código limpo;
- documentação;
- testes;
- portfólio.

A meta é simples:

> Recriar o Jarvis que você já construiu, só que mais organizado, mais bonito, mais revisável, mais mobile e mais profissional.

# Backlog React Native — Jarvis Mobile

## Objetivo

Criar um aplicativo mobile para o **Jarvis Workbench**, feito em React Native, com foco em aprendizado real de mobile e uso prático no celular.

Este backlog existe porque o Jarvis precisa ser confortável no celular desde cedo. A prioridade não é fazer uma tela bonita de desktop. A prioridade é criar uma experiência mobile para:

1. acompanhar jobs longos;
2. importar e visualizar EPUBs;
3. revisar capítulos;
4. revisar alinhamentos EN/PT;
5. aprovar/rejeitar pares;
6. consultar memória;
7. futuramente revisar traduções.

---

# Decisão de stack mobile

## Stack recomendada

* React Native
* Expo
* TypeScript
* Expo Router
* TanStack Query
* React Hook Form
* Zod
* NativeWind ou StyleSheet puro no início
* SecureStore em fase posterior
* AsyncStorage em fase posterior

## Por que Expo

Para seu momento, Expo é o melhor caminho porque reduz dor de cabeça inicial com build nativo. Você aprende React Native, navegação, telas, chamadas HTTP, estado, formulários e UX mobile sem começar brigando com Gradle, Android Studio e configuração nativa.

## Regra importante

O app mobile não deve ter regra de negócio pesada. Ele deve consumir a API Java.

Regra de ouro:

```text
Backend Java decide.
Mobile mostra, coleta ação do usuário e envia comandos.
```

---

# Princípios de UI mobile do Jarvis

## 1. Card antes de tabela

No celular, evitar tabelas grandes. Usar cards empilhados.

## 2. Ação grande e clara

Botões grandes para:

* abrir;
* iniciar job;
* aprovar;
* rejeitar;
* revisar;
* tentar novamente.

## 3. Feedback sempre visível

Jobs longos precisam mostrar:

* status;
* progresso;
* mensagem atual;
* erro, se existir;
* última atualização.

## 4. Leitura confortável

Para revisão EN/PT:

* texto original em um card;
* texto traduzido/alinhado em outro;
* score e flags visíveis;
* navegação próxima/anterior fácil.

## 5. Offline depois, não agora

No início, o app pode depender da API local. Offline e cache avançado entram depois.

---

# Estrutura sugerida do projeto mobile

```text
jarvis-mobile/

  app/
    _layout.tsx
    index.tsx
    jobs/
      index.tsx
      [id].tsx
    epubs/
      index.tsx
      [id].tsx
    book-pairs/
      index.tsx
      [id].tsx
    alignments/
      index.tsx
      [id].tsx
      review.tsx
    memory/
      index.tsx
    settings/
      index.tsx

  src/
    api/
      httpClient.ts
      healthApi.ts
      jobsApi.ts
      epubsApi.ts
      bookPairsApi.ts
      alignmentsApi.ts
      memoryApi.ts

    components/
      AppButton.tsx
      AppCard.tsx
      AppScreen.tsx
      AppTextInput.tsx
      StatusBadge.tsx
      ProgressBar.tsx
      EmptyState.tsx
      ErrorState.tsx
      LoadingState.tsx

    features/
      jobs/
        components/
        hooks/
        types.ts
      epubs/
        components/
        hooks/
        types.ts
      bookPairs/
        components/
        hooks/
        types.ts
      alignments/
        components/
        hooks/
        types.ts
      memory/
        components/
        hooks/
        types.ts

    theme/
      spacing.ts
      colors.ts
      typography.ts
      radius.ts

    config/
      env.ts
```

---

# Épico RN-0 — Fundação mobile

## RN-001 — Criar projeto Expo com TypeScript

### Estória

Como desenvolvedor, quero criar o projeto mobile em Expo com TypeScript para iniciar o app Jarvis Mobile com uma base moderna e simples de evoluir.

### Regras de negócio

* O app deve iniciar no celular/emulador.
* O projeto deve usar TypeScript.
* O app deve ter nome claro: `Jarvis Mobile`.
* Não deve consumir API ainda.

### Critérios de aceite

* Projeto inicia com `npm start` ou comando equivalente.
* App abre no Expo Go ou emulador.
* Tela inicial mostra o nome `Jarvis Mobile`.
* Estrutura inicial de pastas criada.

### O que quero aprender nesta estória

* O que é Expo.
* Diferença entre React web e React Native.
* Componentes básicos: `View`, `Text`, `Pressable`, `ScrollView`.
* Como o app roda no celular.

### O que quero ver no code review

* Estrutura simples.
* Sem libs desnecessárias.
* TypeScript funcionando.
* Código legível.

---

## RN-002 — Criar layout base mobile

### Estória

Como usuário, quero uma tela base consistente para navegar no Jarvis Mobile sem cada página parecer de um jeito.

### Regras de UX

* Toda tela deve ter padding seguro.
* Deve respeitar área segura do celular.
* Deve ter título visível.
* Deve funcionar bem em telas pequenas.

### Componentes esperados

* `AppScreen`
* `AppCard`
* `AppButton`
* `StatusBadge`

### Critérios de aceite

* Tela inicial usa `AppScreen`.
* Existe card reutilizável.
* Existe botão reutilizável.
* Layout fica confortável no celular.

### O que quero aprender

* Safe area.
* Estilização no React Native.
* Componentização mobile.
* Diferença entre `Pressable`, `TouchableOpacity` e botão web.

---

## RN-003 — Configurar navegação com Expo Router

### Estória

Como usuário, quero navegar entre as áreas principais do app para acessar Dashboard, Jobs, EPUBs, BookPairs, Alinhamentos, Memória e Configurações.

### Rotas iniciais

* `/`
* `/jobs`
* `/epubs`
* `/book-pairs`
* `/alignments`
* `/memory`
* `/settings`

### Regras de negócio

* Navegação deve ser simples.
* O usuário deve conseguir voltar.
* O app deve ter menu inferior ou lista de atalhos inicial.

### Critérios de aceite

* Todas as rotas abrem.
* Tela inicial tem atalhos para as áreas principais.
* Navegação funciona no celular.

### O que quero aprender

* Roteamento mobile.
* Stack navigation.
* Tabs ou links.
* Organização por arquivos no Expo Router.

---

## RN-004 — Configurar tema visual mínimo

### Estória

Como desenvolvedor, quero criar um tema mínimo para manter espaçamentos, tamanhos e cores consistentes no app.

### Itens do tema

* espaçamentos;
* bordas;
* tamanhos de fonte;
* cores de status;
* sombras simples.

### Critérios de aceite

* Componentes usam tema centralizado.
* Status diferentes têm aparência consistente.
* Não existem números mágicos espalhados demais.

### O que quero aprender

* Design tokens.
* Como evitar UI bagunçada.
* Como pensar visualmente no celular.

---

# Épico RN-1 — Conexão com API Java

## RN-010 — Configurar API base local

### Estória

Como app mobile, quero saber a URL da API Java para consumir os endpoints do Jarvis Workbench.

### Regras de negócio

* A URL da API deve ficar centralizada.
* Deve ser fácil trocar entre IP local, localhost/emulador e produção futura.
* Erros de conexão devem ser tratados.

### Arquivos esperados

```text
src/config/env.ts
src/api/httpClient.ts
```

### Critérios de aceite

* URL da API está centralizada.
* Existe client HTTP reutilizável.
* Erro de rede retorna mensagem amigável.

### O que quero aprender

* Diferença entre localhost no PC e no celular.
* Chamada HTTP no React Native.
* Organização de client API.

---

## RN-011 — Consumir Health Check

### Estória

Como usuário, quero ver se o backend Jarvis está online pelo app mobile.

### Endpoint backend

```http
GET /api/health
```

### Regras de negócio

* Dashboard deve mostrar status da API.
* Se a API estiver fora, mostrar erro amigável.
* Deve existir botão de tentar novamente.

### Critérios de aceite

* App chama `/api/health`.
* Mostra `UP` quando backend responde.
* Mostra erro quando backend está offline.
* Botão retry funciona.

### O que quero aprender

* `fetch` ou client HTTP.
* Loading state.
* Error state.
* Retry manual.

---

## RN-012 — Introduzir TanStack Query

### Estória

Como desenvolvedor, quero usar TanStack Query para controlar loading, erro, cache e refetch das chamadas HTTP.

### Regras de negócio

* Health check deve usar query.
* Jobs futuros devem usar query.
* Não espalhar `useEffect` para toda chamada de API.

### Critérios de aceite

* Provider configurado.
* Health check usa `useQuery`.
* Loading e erro continuam funcionando.

### O que quero aprender

* Cache de dados.
* `useQuery`.
* Refetch.
* Separação entre API e tela.

---

# Épico RN-2 — Jobs mobile

## RN-020 — Tela mobile de listagem de jobs

### Estória

Como usuário, quero ver os jobs do Jarvis em cards para acompanhar importações e alinhamentos pelo celular.

### Endpoint esperado

```http
GET /api/jobs?page=0&size=20
```

### Regras de UX

* Não usar tabela.
* Cada job deve aparecer como card.
* Mostrar tipo, status, progresso e mensagem.
* Cards devem ser tocáveis.

### Critérios de aceite

* Lista jobs vindos da API.
* Mostra loading.
* Mostra erro.
* Mostra empty state.
* Ao tocar no card, abre detalhe.

### O que quero aprender

* FlatList.
* Cards clicáveis.
* Paginação simples.
* Estados de tela.

---

## RN-021 — Tela de detalhe do job

### Estória

Como usuário, quero abrir um job e ver progresso, status, mensagem e erro para entender o que está acontecendo.

### Endpoint esperado

```http
GET /api/jobs/{publicId}
```

### Regras de UX

* Mostrar progresso visual.
* Mostrar timeline resumida quando existir.
* Mostrar erro em destaque.
* Botão para atualizar.

### Critérios de aceite

* Detalhe carrega pelo ID.
* Erro 404 aparece de forma amigável.
* Refresh manual funciona.

### O que quero aprender

* Rotas dinâmicas.
* Parâmetros de rota.
* Progress bar.
* Tratamento de erro por status HTTP.

---

## RN-022 — Auto-refresh para job em execução

### Estória

Como usuário, quero que jobs em execução atualizem automaticamente para acompanhar progresso sem ficar apertando refresh.

### Regras de negócio

* Auto-refresh só para jobs `PENDING` ou `RUNNING`.
* Jobs finalizados não devem continuar atualizando sem necessidade.
* Intervalo inicial sugerido: 3 a 5 segundos.

### Critérios de aceite

* Job em execução atualiza sozinho.
* Job concluído para de atualizar.
* Não causa chamadas infinitas desnecessárias.

### O que quero aprender

* Refetch interval.
* Evitar polling exagerado.
* Condicionar comportamento pelo status.

---

## RN-023 — Timeline mobile de eventos do job

### Estória

Como usuário, quero ver os eventos de um job para entender o histórico da importação ou alinhamento.

### Endpoint esperado

```http
GET /api/jobs/{publicId}/events
```

### Regras de UX

* Eventos aparecem em lista vertical.
* Nível `ERROR` deve se destacar.
* Mensagens longas devem quebrar linha.
* Detalhes técnicos podem ficar recolhidos.

### Critérios de aceite

* Lista eventos.
* Loading/erro/empty state funcionam.
* Eventos são ordenados corretamente.

### O que quero aprender

* Listas cronológicas.
* UI de logs no mobile.
* Conteúdo expansível.

---

# Épico RN-3 — EPUBs no celular

## RN-030 — Tela de lista de EPUBs

### Estória

Como usuário, quero ver todos os EPUBs importados em cards para escolher qual abrir.

### Endpoint esperado

```http
GET /api/epubs?page=0&size=20
```

### Informações no card

* título ou nome do arquivo;
* idioma;
* status;
* capítulos;
* parágrafos;
* data de importação.

### Critérios de aceite

* Lista EPUBs.
* Cards são legíveis no celular.
* Empty state orienta o usuário a importar um EPUB.

### O que quero aprender

* Modelagem de DTO no frontend.
* Listas mobile.
* Separação entre feature e componente.

---

## RN-031 — Upload de EPUB pelo celular

### Estória

Como usuário, quero selecionar um arquivo EPUB no celular e enviar para o backend.

### Regras de negócio

* Aceitar apenas arquivo `.epub`.
* Mostrar nome do arquivo antes de enviar.
* Mostrar progresso ou estado de envio.
* Exibir erro amigável se falhar.
* Ao sucesso, mostrar job criado.

### Endpoint backend

```http
POST /api/epubs/upload
```

### Critérios de aceite

* Usuário seleciona arquivo.
* App envia multipart para API.
* Upload válido cria job.
* Upload inválido mostra erro.

### O que quero aprender

* Seleção de arquivos no mobile.
* Multipart upload.
* Permissões.
* Diferença entre URI local e arquivo real.

### Observação

Esta estória é mais difícil que listagem. Se travar, podemos primeiro fazer upload pelo Insomnia e usar o mobile só para visualizar.

---

## RN-032 — Tela de detalhe do EPUB

### Estória

Como usuário, quero abrir um EPUB e ver metadados, capítulos e resumo de extração.

### Endpoint esperado

```http
GET /api/epubs/{id}
```

### Regras de UX

* Mostrar metadados no topo.
* Mostrar contadores em cards.
* Mostrar capítulos em lista recolhível.
* Mostrar botão para iniciar/reprocessar extração quando permitido.

### Critérios de aceite

* Detalhe carrega pelo ID.
* Capítulos aparecem em ordem.
* Usuário consegue abrir preview de capítulo.

---

## RN-033 — Preview de capítulo e parágrafos

### Estória

Como usuário, quero abrir um capítulo e visualizar seus parágrafos para validar se a importação ficou boa.

### Regras de UX

* Parágrafos aparecem em cards pequenos.
* Índice global e índice no capítulo devem aparecer.
* Texto longo deve ser legível.
* Deve ter paginação ou carregamento gradual.

### Critérios de aceite

* Lista parágrafos do capítulo.
* Ordem correta.
* Scroll confortável.

### O que quero aprender

* Exibição de texto longo.
* Performance com listas.
* Paginação mobile.

---

# Épico RN-4 — BookPair mobile

## RN-040 — Lista de pares de livros

### Estória

Como usuário, quero ver os pares EN/PT criados para escolher um fluxo de alinhamento.

### Endpoint esperado

```http
GET /api/book-pairs?page=0&size=20
```

### Informações no card

* nome do par;
* idioma origem e destino;
* EPUB origem;
* EPUB destino;
* status;
* último job;
* próximo passo.

### Critérios de aceite

* Lista book pairs.
* Card mostra estado geral.
* Tocar abre dashboard do par.

---

## RN-041 — Criar BookPair pelo celular

### Estória

Como usuário, quero selecionar dois EPUBs importados e criar um par EN/PT pelo app mobile.

### Regras de negócio

* Usuário deve escolher EPUB origem.
* Usuário deve escolher EPUB destino.
* Não pode escolher o mesmo EPUB duas vezes.
* Idiomas devem ser preenchidos.
* Nome do par é obrigatório.

### Critérios de aceite

* Formulário valida dados.
* Erros aparecem perto dos campos.
* Envio cria BookPair.
* Ao sucesso, navega para detalhe.

### O que quero aprender

* React Hook Form.
* Zod.
* Select/lista no mobile.
* Validação de formulário.

---

## RN-042 — Dashboard mobile do BookPair

### Estória

Como usuário, quero ver o estado geral do par de livros para decidir o próximo passo.

### Informações

* quantidade de capítulos EN/PT;
* quantidade de parágrafos EN/PT;
* status do mapeamento de capítulos;
* status do alinhamento;
* últimos jobs;
* ações recomendadas.

### Critérios de aceite

* Dashboard é legível no celular.
* Ações principais são claras.
* Usuário entende se já pode alinhar.

---

# Épico RN-5 — Mapeamento de capítulos mobile

## RN-050 — Listar mapa de capítulos

### Estória

Como usuário, quero ver os capítulos EN/PT mapeados para revisar antes do alinhamento de parágrafos.

### Regras de UX

* Cada mapeamento aparece como card.
* Mostrar capítulo EN e capítulo PT.
* Mostrar score.
* Mostrar flag de suspeito.
* Mostrar status de revisão.

### Critérios de aceite

* Lista mapeamentos.
* Cards são legíveis no celular.
* Filtro por suspeitos funciona.

---

## RN-051 — Revisar par de capítulos

### Estória

Como usuário, quero abrir um mapeamento de capítulos e aprovar, rejeitar ou ajustar o par.

### Regras de UX

* Mostrar preview do texto EN.
* Mostrar preview do texto PT.
* Mostrar score e motivo das flags.
* Botões grandes: aprovar, rejeitar, ajustar.

### Critérios de aceite

* Usuário aprova mapeamento.
* Usuário rejeita mapeamento.
* Status muda na API.

---

# Épico RN-6 — Revisão de alinhamento mobile

## RN-060 — Lista de alignment runs

### Estória

Como usuário, quero ver as execuções de alinhamento de um BookPair para escolher qual revisar.

### Informações no card

* algoritmo;
* status;
* data;
* total de pares;
* score médio;
* suspeitos;
* aprovados.

### Critérios de aceite

* Lista runs.
* Card mostra resumo útil.
* Tocar abre detalhe.

---

## RN-061 — Resumo da alignment run

### Estória

Como usuário, quero ver um resumo de qualidade da run antes de revisar pares.

### Métricas

* total de pares;
* score médio;
* pares suspeitos;
* skips;
* tipos de alinhamento;
* capítulos problemáticos.

### Critérios de aceite

* Resumo aparece em cards.
* Usuário consegue ir para revisão.
* Filtros iniciais estão disponíveis.

---

## RN-062 — Tela de revisão de pares alinhados

### Estória

Como usuário, quero revisar pares EN/PT no celular, um por vez ou em lista, para aprovar/rejeitar com conforto.

### Regras de UX

* Texto EN em card.
* Texto PT em card.
* Score visível.
* Flags visíveis.
* Tipo do alinhamento visível.
* Botões grandes: aprovar, rejeitar, suspeito, próximo.
* Deve ser confortável para leitura no celular.

### Critérios de aceite

* Carrega pares da run.
* Aprova par.
* Rejeita par.
* Marca suspeito.
* Navega próximo/anterior.

### O que quero aprender

* UX de revisão.
* Estado local temporário.
* Mutations com TanStack Query.
* Atualização otimista ou refetch após ação.

---

## RN-063 — Filtros de revisão

### Estória

Como usuário, quero filtrar pares por score, flag, capítulo e tipo para revisar primeiro os problemáticos.

### Filtros iniciais

* score baixo;
* suspeitos;
* skip;
* 1→2;
* 2→1;
* capítulo;
* não revisados.

### Critérios de aceite

* Filtros alteram lista.
* Filtro ativo aparece claramente.
* É fácil limpar filtros.

---

## RN-064 — Edição manual de par alinhado

### Estória

Como usuário, quero editar manualmente o texto de um par alinhado antes de aprovar para memória.

### Regras de negócio

* Edição não deve apagar texto original sem rastreabilidade.
* Deve salvar versão revisada.
* Deve registrar que houve edição humana.

### Critérios de aceite

* Usuário edita EN/PT revisado.
* Salva alteração.
* API mantém original e revisado.

### Observação

Essa estória depende de suporte no backend.

---

# Épico RN-7 — Memória mobile

## RN-070 — Busca textual na memória

### Estória

Como usuário, quero buscar exemplos aprovados na memória pelo celular.

### Regras de UX

* Campo de busca grande.
* Resultados em cards.
* Mostrar EN/PT.
* Mostrar origem/provenance.

### Critérios de aceite

* Busca chama API.
* Loading/erro/empty state funcionam.
* Resultados são legíveis.

---

## RN-071 — Detalhe do par de memória

### Estória

Como usuário, quero abrir um par de memória e ver sua origem, nível, scores e texto completo.

### Critérios de aceite

* Detalhe mostra EN/PT completo.
* Mostra se veio de parágrafo ou sentença.
* Mostra livro/run de origem.

---

# Épico RN-8 — Tradução/revisão mobile

## RN-080 — Tela de tradução assistida

### Estória

Como usuário, quero revisar uma sugestão de tradução no celular comparando original, IA e memórias recuperadas.

### Regras de UX

* Original no topo.
* Tradução sugerida em campo editável.
* Memórias em acordeão.
* Botões grandes: aprovar, editar, rejeitar, próxima.

### Critérios de aceite

* Exibe tradução sugerida.
* Exibe contexto usado.
* Permite aprovar versão final.

---

## RN-081 — Histórico de traduções

### Estória

Como usuário, quero ver traduções anteriores para acompanhar o progresso do projeto.

### Critérios de aceite

* Lista TranslationRuns.
* Mostra status e modelo usado.
* Abre detalhe.

---

# Épico RN-9 — Configurações mobile

## RN-090 — Configurar URL da API pelo app

### Estória

Como usuário, quero configurar a URL da API Java no app para conectar com meu backend local.

### Regras de negócio

* Campo deve aceitar IP/porta.
* Deve ter botão testar conexão.
* Deve salvar configuração localmente.

### Critérios de aceite

* Usuário altera URL.
* Configuração é persistida.
* Health check usa nova URL.

### O que quero aprender

* AsyncStorage ou SecureStore.
* Configuração local.
* Teste de conexão.

---

## RN-091 — Tela Sobre o Jarvis Mobile

### Estória

Como usuário, quero ver informações do app, versão e backend conectado.

### Critérios de aceite

* Mostra versão do app.
* Mostra URL da API.
* Mostra status do backend.

---

# Ordem recomendada de desenvolvimento mobile

## Sprint Mobile 0 — Fundação

1. RN-001 — Criar projeto Expo com TypeScript.
2. RN-002 — Criar layout base mobile.
3. RN-003 — Configurar navegação com Expo Router.
4. RN-004 — Configurar tema visual mínimo.

## Sprint Mobile 1 — API e health

1. RN-010 — Configurar API base local.
2. RN-011 — Consumir Health Check.
3. RN-012 — Introduzir TanStack Query.

## Sprint Mobile 2 — Jobs

1. RN-020 — Tela mobile de listagem de jobs.
2. RN-021 — Tela de detalhe do job.
3. RN-022 — Auto-refresh para job em execução.
4. RN-023 — Timeline mobile de eventos do job.

## Sprint Mobile 3 — EPUBs

1. RN-030 — Tela de lista de EPUBs.
2. RN-032 — Tela de detalhe do EPUB.
3. RN-033 — Preview de capítulo e parágrafos.
4. RN-031 — Upload de EPUB pelo celular.

Observação: upload pode ser feito depois de listagem/detalhe porque é mais difícil.

## Sprint Mobile 4 — BookPair

1. RN-040 — Lista de pares de livros.
2. RN-041 — Criar BookPair pelo celular.
3. RN-042 — Dashboard mobile do BookPair.

## Sprint Mobile 5 — Capítulos

1. RN-050 — Listar mapa de capítulos.
2. RN-051 — Revisar par de capítulos.

## Sprint Mobile 6 — Alinhamento

1. RN-060 — Lista de alignment runs.
2. RN-061 — Resumo da alignment run.
3. RN-062 — Tela de revisão de pares alinhados.
4. RN-063 — Filtros de revisão.
5. RN-064 — Edição manual de par alinhado.

## Sprint Mobile 7 — Memória

1. RN-070 — Busca textual na memória.
2. RN-071 — Detalhe do par de memória.

## Sprint Mobile 8 — Tradução

1. RN-080 — Tela de tradução assistida.
2. RN-081 — Histórico de traduções.

## Sprint Mobile 9 — Configurações

1. RN-090 — Configurar URL da API pelo app.
2. RN-091 — Tela Sobre o Jarvis Mobile.

---

# Como pedir ajuda no mobile

## Para detalhar uma estória

```text
Chefe, vamos detalhar a RN-001 antes de codar.
Me diga exatamente arquivos, dependências, decisões, regras e critérios de aceite.
```

## Para code review

```text
Chefe, faz code review da RN-001.
Arquivos alterados:
...
```

## Para dúvida conceitual

```text
Chefe, estou na RN-002 e não entendi a diferença entre View, ScrollView e SafeAreaView.
Me explica como LT/professor.
```

## Para erro

```text
Chefe, estou na RN-011 e deu esse erro no celular:
[erro]
Meu env.ts está assim:
...
Meu httpClient está assim:
...
```

---

# Decisão final

O Jarvis terá dois clientes possíveis no futuro:

1. **Jarvis Mobile**, em React Native, prioridade para seu uso no celular.
2. **Jarvis Web**, em React, possível fase posterior.

A prioridade agora, depois da base backend, será aprender mobile de verdade, porque é onde você sente mais necessidade prática.

O mobile não é enfeite. Ele será parte real do produto Jarvis.
