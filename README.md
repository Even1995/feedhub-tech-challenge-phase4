# FeedHub

Plataforma serverless para coleta e gestão de feedbacks acadêmicos (avaliações de alunos), construída com **Java 21**, **Spring Boot 3**, **AWS Lambda**, **DynamoDB**, **SNS**, **EventBridge** e **Terraform**, seguindo **Clean Architecture**, princípios **SOLID** e os padrões **DTO**, **Repository** e **Use Case**.

## Sumário

- [Arquitetura](#arquitetura)
- [Estrutura do repositório](#estrutura-do-repositório)
- [Módulos](#módulos)
- [Regra de urgência](#regra-de-urgência)
- [Segurança e perfis](#segurança-e-perfis)
- [Endpoints da API](#endpoints-da-api)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [Build](#build)
- [Deploy (Terraform)](#deploy-terraform)
- [Testes locais](#testes-locais)
- [Credenciais padrão](#credenciais-padrão)

## Arquitetura

O FeedHub é composto por **4 funções AWS Lambda** independentes, que compartilham código comum através do módulo `feedhub-common`:

| Lambda | Trigger | Responsabilidade |
|---|---|---|
| `auth-service` | API Gateway (HTTP API) | Registro e login de usuários, emissão de JWT |
| `feedback-service` | API Gateway (HTTP API) | CRUD de avaliações (feedbacks), cálculo de urgência, publicação em SNS |
| `notification-service` | SNS (assíncrono) | Envio de e-mail (SES) para feedbacks de urgência **ALTA** |
| `report-service` | EventBridge (agendado semanalmente) | Geração e envio por e-mail do relatório semanal de avaliações |

```
Cliente ──▶ API Gateway (HTTP API) ──▶ auth-service / feedback-service ──▶ DynamoDB
                                                  │
                                                  ▼ (urgência ALTA)
                                            SNS Topic ──▶ notification-service ──▶ SES
EventBridge (rate 7 dias) ──▶ report-service ──▶ DynamoDB (scan) ──▶ SES
```

Cada serviço segue **Clean Architecture**, organizado em camadas:

- `domain/entity` — entidades e regras de negócio puras.
- `domain/repository` — portas (interfaces) de persistência.
- `application/dto` — objetos de transporte de dados (request/response).
- `application/usecase` (+ `impl`) — casos de uso (regras de aplicação).
- `application/port` — portas de saída para integrações externas (SNS, SES).
- `infrastructure/persistence` — adaptadores DynamoDB.
- `infrastructure/messaging` — adaptadores SNS/SES.
- `infrastructure/config` — configuração Spring (segurança, clientes AWS).
- `presentation` — controllers REST ou handlers de evento (SNS/EventBridge).

## Estrutura do repositório

```
pom.xml                        # POM pai (multi-módulo Maven)
infra/                          # Infraestrutura como código (Terraform)
  modules/lambda/               # Módulo reutilizável de função Lambda
  main.tf, variables.tf, outputs.tf, versions.tf
src/
  feedhub-common/               # Módulo compartilhado (segurança, exceptions, utils)
  auth-service/                 # Lambda HTTP: registro/login
  feedback-service/             # Lambda HTTP: avaliações
  notification-service/         # Lambda SNS: notificação crítica por e-mail
  report-service/                # Lambda EventBridge: relatório semanal
```

## Módulos

### feedhub-common

Código compartilhado entre os serviços: `JwtTokenProvider`, `JwtAuthenticationFilter`, `PasswordEncoderConfig` (BCrypt), `GlobalExceptionHandler`, `ApiResponse`, `UrgenciaUtil`, `DateUtil`, `LoggingUtil`, constantes da aplicação.

### auth-service

- `POST /auth/register` — cria um novo usuário (perfil `ALUNO` por padrão).
- `POST /auth/login` — autentica e retorna um token JWT válido por **1 hora**.
- Semeia automaticamente um usuário `ADMIN` na inicialização (ver [Credenciais padrão](#credenciais-padrão)).
- Senhas são armazenadas com **BCrypt**.

### feedback-service

- `POST /avaliacoes` — cria uma avaliação (nota 0–10 + comentário). A urgência é calculada automaticamente.
- `GET /avaliacoes` — lista avaliações (ADMIN vê todas; ALUNO vê apenas as próprias).
- `GET /avaliacoes/{id}` — detalha uma avaliação (checagem de propriedade/perfil).
- Ao criar uma avaliação com urgência **ALTA**, publica um evento no tópico SNS de notificações críticas.

### notification-service

Consome mensagens do tópico SNS de notificações críticas e envia um e-mail (via SES) alertando sobre o feedback urgente.

### report-service

Executado semanalmente via regra do EventBridge. Varre as avaliações dos últimos 7 dias, calcula total, nota média e contagem por nível de urgência, e envia o resumo por e-mail (via SES).

## Regra de urgência

A urgência de uma avaliação é derivada da nota (0–10):

| Nota | Urgência |
|---|---|
| 0 – 2 | **ALTA** |
| 3 – 4 | **MEDIA** |
| 5 – 10 | **BAIXA** |

Avaliações com urgência **ALTA** disparam notificação por e-mail automaticamente.

## Segurança e perfis

- Autenticação **stateless** via **JWT** (HMAC-SHA), com expiração de **1 hora**.
- Perfis suportados: `ADMIN` e `ALUNO`.
  - `ADMIN`: acesso a todas as avaliações.
  - `ALUNO`: acesso apenas às próprias avaliações.
- Senhas com hashing **BCrypt**.

## Endpoints da API

### Auth Service

**`POST /auth/register`**
```json
{
  "nome": "Maria Silva",
  "email": "maria@aluno.com",
  "senha": "SenhaForte@123"
}
```

**`POST /auth/login`**
```json
{
  "email": "maria@aluno.com",
  "senha": "SenhaForte@123"
}
```
Resposta:
```json
{
  "token": "eyJhbGciOi...",
  "expiraEm": 3600
}
```

### Feedback Service

Requer header `Authorization: Bearer <token>`.

**`POST /avaliacoes`**
```json
{
  "nota": 2,
  "comentario": "O curso precisa de mais conteúdo prático."
}
```

**`GET /avaliacoes`** — lista avaliações (filtradas por perfil).

**`GET /avaliacoes/{id}`** — detalha uma avaliação específica.

## Variáveis de ambiente

| Variável | Serviço(s) | Descrição |
|---|---|---|
| `JWT_SECRET` | auth, feedback | Chave usada para assinar/validar os tokens JWT |
| `USUARIOS_TABLE` | auth | Nome da tabela DynamoDB de usuários |
| `AVALIACOES_TABLE` | feedback, report | Nome da tabela DynamoDB de avaliações |
| `DYNAMODB_ENDPOINT` | auth, feedback, report | Endpoint customizado (ex.: DynamoDB Local); vazio em produção |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` / `ADMIN_NOME` | auth | Credenciais do usuário administrador semeado na inicialização |
| `SNS_CRITICAL_TOPIC_ARN` | feedback | ARN do tópico SNS de notificações críticas |
| `SES_SENDER_EMAIL` | notification, report | E-mail remetente verificado no SES |
| `NOTIFICATION_RECIPIENT_EMAIL` | notification | Destinatário das notificações críticas |
| `REPORT_RECIPIENT_EMAIL` | report | Destinatário do relatório semanal |
| `AWS_REGION_NAME` | todos | Região AWS utilizada pelos clientes AWS SDK |

## Build

Pré-requisitos: **JDK 21** e **Maven 3.8+**.

```powershell
mvn -DskipTests package
```

O comando compila todos os módulos e gera os artefatos (shaded JARs) usados pelas Lambdas em `src/<serviço>/target/<serviço>.jar`.

## Deploy (Terraform)

Pré-requisitos: **Terraform >= 1.5**, build dos JARs concluído, credenciais AWS configuradas, identidade de e-mail verificada no **SES** (sandbox ou produção).

```powershell
cd infra
terraform init
terraform plan -var="jwt_secret=<segredo-forte>"
terraform apply -var="jwt_secret=<segredo-forte>"
```

Recursos provisionados:
- 4 funções Lambda (auth, feedback, notification, report) com roles/políticas IAM dedicadas e log groups no CloudWatch.
- Tabelas DynamoDB `usuarios` (GSI `email-index`) e `avaliacoes` (GSI `alunoEmail-index`).
- Tópico SNS para notificações críticas, com assinatura Lambda.
- Regra do EventBridge (`rate(7 days)`) para o relatório semanal.
- API Gateway HTTP API com rotas `/auth/{proxy+}` e `/avaliacoes{,/{proxy+}}`.

Ao final, a URL base da API é exposta no output `api_gateway_endpoint`.

## Testes locais

Para rodar os serviços HTTP localmente (fora da Lambda) ou com DynamoDB Local, defina `DYNAMODB_ENDPOINT=http://localhost:8000` e aponte o cliente AWS SDK para uma instância local do DynamoDB. Os testes de unidade de cada caso de uso podem ser executados com:

```powershell
mvn test
```

## Credenciais padrão

Ao iniciar, o `auth-service` semeia automaticamente um usuário administrador (caso ainda não exista):

- **E-mail:** `admin@feedhub.com`
- **Senha:** `Admin@123`
- **Perfil:** `ADMIN`

> Altere essas credenciais em produção através das variáveis `ADMIN_EMAIL` e `ADMIN_PASSWORD`.
