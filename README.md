# 🛒 Good Look — E-commerce API

API REST de um e-commerce completo desenvolvido como projeto prático 
da disciplina de Programação para Web — IFG Campus Luziânia.

## 🚀 Tecnologias

- **Java 21** — linguagem principal
- **Quarkus 3** — framework back-end
- **PostgreSQL 17** — banco de dados
- **Docker & Docker Compose** — conteinerização
- **JWT** — autenticação e autorização
- **Hibernate ORM Panache** — persistência de dados

## 🏗️ Padrões de Projeto

- **MVC** — separação de responsabilidades
- **Entity / DAO / BO / DTO** — camadas bem definidas
- **RBAC** — controle de acesso por perfil (Admin Master, Admin, Cliente)
- **Conventional Commits** — padronização do histórico Git
- **GitFlow** — gestão de branches

## 🔒 Segurança

- Autenticação via JWT
- Autorização por perfil de usuário
- Proteção contra IDOR
- Sanitização de inputs (XSS)
- Log de auditoria de todas as ações

## 📋 Pré-requisitos

- Docker
- Docker Compose

## ▶️ Como executar

Clone o repositório:
```bash
git clone https://github.com/renanamancio/Good-Look.git
cd Good-Look
```

Suba o ambiente completo:
```bash
docker compose up --build
```

Acesse:
- **Site:** http://localhost
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger

## 📁 Estrutura do Projeto

src/main/java/com/ecommerce/
├── entity/     → Modelos de dados
├── dao/        → Acesso a dados
├── bo/         → Regras de negócio
├── dto/        → Transferência de dados
└── resource/   → Endpoints REST

## 📊 Testes

- **Unitários** — JUnit 5 + Mockito
- **Integração** — Quarkus Test + Testcontainers
- **Sistema** — REST Assured

## 📜 Licença

MIT