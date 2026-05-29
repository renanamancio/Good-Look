# 🛒 Contexto do Projeto: E-Commerce (IFG - Campus Luziânia)

## 🎯 Visão Geral do Projeto
[cite_start]Este projeto consiste na implementação de um software completo para Web[cite: 2], focado em um domínio de **E-commerce**. [cite_start]O objetivo é aplicar os fundamentos de Programação para Web do Bacharelado em Sistemas de Informação[cite: 1, 12].

---

## 💻 Stack Tecnológica
* [cite_start]**Back-end:** Java 25 (Java EE/Jakarta EE) [cite: 16, 17] [cite_start]com o framework Quarkus[cite: 20].
* [cite_start]**Front-end:** Angular [cite: 40] [cite_start]e Bootstrap[cite: 40].
* [cite_start]**Banco de Dados:** PostgreSQL[cite: 40].
* [cite_start]**Comunicação:** API REST via JAX-RS[cite: 19].
* **DevOps:** GitHub Actions (CI/CD) e Fluxo Gitflow.

---

## 🏛️ Requisitos Arquiteturais e Padrões (Obrigatórios)
O projeto deve seguir rigorosamente os seguintes padrões de desenvolvimento:
* [cite_start]**Modelo MVC:** O sistema deve utilizar obrigatoriamente o modelo de desenvolvimento em camadas[cite: 18].
* [cite_start]**Entity & DAO:** Para cada entidade, deve haver uma classe `Entity` (estrutura de dados) e um respectivo objeto `DAO` (Data Access Object) para persistência e recuperação[cite: 21, 22].
* [cite_start]**Business Object (BO):** TODAS as regras de negócio e validações devem ser implementadas exclusivamente em objetos BO[cite: 23, 24].
* [cite_start]**DTO (Data Transfer Object):** A comunicação entre back-end e front-end deve ocorrer exclusivamente por DTOs por motivos de segurança; entidades nunca devem ser enviadas à interface[cite: 26, 27].

---

## 📋 Requisitos Funcionais (RF)
| ID | Requisito | Descrição |
| :--- | :--- | :--- |
| **RF01** | **Autenticação** | [cite_start]Solicitar e-mail e senha para acesso à página principal[cite: 4, 5]. |
| **RF02** | **Manter Usuário** | [cite_start]Cadastro e gestão de usuários que acessam a aplicação[cite: 6]. |
| **RF03** | **Manter Perfil** | [cite_start]Cadastro de perfis com permissões específicas para cada recurso funcional[cite: 7]. |
| **RF04** | **Navegação** | [cite_start]Mecanismo de navegação (links/menu) para retornar ao passo anterior ou principal[cite: 8, 9]. |
| **RF05** | **Rastreabilidade/Auditoria** | [cite_start]Log de todas as ações: ação executada, usuário executor e data/hora[cite: 15]. |
| **RF06** | **Checkout (Domínio 1)** | [cite_start]Fluxo de compra de produtos por usuários autenticados[cite: 10, 14]. |
| **RF07** | **Gestão de Produtos (Domínio 2)** | [cite_start]Cadastro de produtos acessível apenas por administradores[cite: 10, 14]. |

---

## 🧪 Estratégia de Testes (Cobertura em Todas as Camadas)
* **Back-end:** * Testes de Unidade (JUnit) para regras de negócio nos BOs.
    * Testes de Integração para camadas DAO e Persistência.
    * Testes de Endpoint (RestAssured) para validar JAX-RS e DTOs.
* **Front-end:** * Testes de componentes e serviços em Angular.
    * Testes E2E para fluxos críticos (Login e Checkout).

---

## 🚀 Fluxo de Trabalho e CI/CD
* **Gitflow:**
    * `main`: Versão estável de entrega.
    * `develop`: Integração de funcionalidades.
    * `feature/*`: Desenvolvimento de requisitos específicos.
* **GitHub Actions:**
    * Automação de Build e execução de Testes a cada Pull Request para `develop`.
    * Automação de Deploy para produção ao realizar merge na `main`.

---

## 📊 Critérios de Avaliação e Nota Final
[cite_start]A nota final individual (**NF**) é calculada pela fórmula[cite: 32]:
$$NF = (FE + BE + AF + FC) \times \frac{AA}{10}$$

* [cite_start]**FE (Front-End):** 2 pontos[cite: 28].
* [cite_start]**BE (Back-end):** 2 pontos[cite: 29].
* [cite_start]**AF (Fundamentos):** 2 pontos[cite: 29].
* [cite_start]**FC (Fluxo Completo):** 4 pontos[cite: 30].
* [cite_start]**AA (Arguição):** 10 pontos[cite: 31].
