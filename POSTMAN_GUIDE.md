# 🚀 Guia de Testes da API no Postman

> [!IMPORTANT]
> **Correção no `import.sql` Aplicada com Sucesso:**
> O arquivo `import.sql` estava localizado em `src/main/resources/META-INF/import.sql`. No ecossistema do Quarkus com Hibernate ORM, o script padrão de inicialização deve estar localizado na raiz da pasta de recursos (`src/main/resources/import.sql`). 
> O arquivo foi movido para o local correto. Agora, ao inicializar a aplicação, as roles, os usuários (`admin@testshop.com` / `user@testshop.com`), clientes, endereços e produtos serão semeados automaticamente no banco de dados.

---

## 🔑 Credenciais Padrão do Sistema
* **Administrador (Role: `ADMIN`):**
  * **E-mail:** `admin@testshop.com`
  * **Senha:** `admin123`
* **Cliente Padrão (Role: `USER`):**
  * **E-mail:** `user@testshop.com`
  * **Senha:** `user123`

---

## 🌐 Configuração Global no Postman
1. Defina uma variável de ambiente ou substitua `{{base_url}}` por: `http://localhost:8080`
2. Para endpoints que exigem autenticação, adicione o cabeçalho:
   * **Key:** `Authorization`
   * **Value:** `Bearer <COLE_O_TOKEN_JWT_AQUI>`
   * *Alternativamente, configure a aba **Authorization** do Postman como tipo `Bearer Token` e insira o token obtido no login.*

---

## 📂 1. Autenticação (`/auth`)

### 🔹 POST - Login
* **URL:** `{{base_url}}/auth/login`
* **Headers:** `Content-Type: application/json`
* **Corpo (JSON):**
  ```json
  {
    "email": "admin@testshop.com",
    "senha": "admin123"
  }
  ```
* **Retorno Esperado (200 OK):**
  ```json
  {
    "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIs...",
    "user": {
      "id": "3e000000-0000-0000-0000-000000000001",
      "email": "admin@testshop.com",
      "role": "ADMIN"
    },
    "expiresIn": 28800
  }
  ```

### 🔹 POST - Registro de Novo Usuário
* **URL:** `{{base_url}}/auth/register`
* **Headers:** `Content-Type: application/json`
* **Corpo (JSON):**
  ```json
  {
    "nome": "João da Silva",
    "email": "novo_cliente@testshop.com",
    "senha": "senhaforte123",
    "administrador": false
  }
  ```
* **Retorno Esperado (201 Created):** Retorna o token de acesso e os dados do usuário criado.

### 🔹 POST - Logout
* **URL:** `{{base_url}}/auth/logout`

---

## 🛒 2. Carrinho de Compras (`/api/carrinho`)

> [!NOTE]
> Requer cabeçalho `Authorization: Bearer <JWT>` obtido no login.

### 🔹 GET - Obter Carrinho Atual
* **URL:** `{{base_url}}/api/carrinho`
* **Retorno Esperado (200 OK):** Retorna a lista de itens, preço total e quantidade total.

### 🔹 POST - Adicionar Item ao Carrinho
* **URL:** `{{base_url}}/api/carrinho/itens`
* **Query Params:**
  * `produtoId`: UUID do produto (ex: `2e000000-0000-0000-0000-000000000001`)
  * `quantidade`: `2`
* **Retorno Esperado (200 OK):** Item inserido ou quantidade atualizada.

### 🔹 DELETE - Remover Item do Carrinho
* **URL:** `{{base_url}}/api/carrinho/itens/{produtoId}`
* **Path Variables:** `{produtoId}` = UUID do produto.
* **Retorno Esperado (204 No Content):** Remoção completa do item do carrinho.

### 🔹 DELETE - Limpar Carrinho
* **URL:** `{{base_url}}/api/carrinho`
* **Retorno Esperado (204 No Content):** Esvazia o carrinho de compras.

---

## 📦 3. Catálogo de Produtos (`/admin/produtos`)

### 🔹 GET - Listar Todos os Produtos (Público)
* **URL:** `{{base_url}}/admin/produtos/api`
* **Retorno Esperado (200 OK):** Array com a lista de produtos semeados no banco.

### 🔹 GET - Buscar Produto por ID (Público)
* **URL:** `{{base_url}}/admin/produtos/api/{id}`
* **Path Variables:** `{id}` = UUID do produto.

### 🔹 POST - Criar Novo Produto (Apenas ADMIN)
* **URL:** `{{base_url}}/admin/produtos/api`
* **Headers:** `Authorization: Bearer <JWT_DO_ADMIN>` e `Content-Type: application/json`
* **Corpo (JSON):**
  ```json
  {
    "nome": "Teclado Mecânico RGB",
    "descricao": "Teclado mecânico switch brown com layout ABNT2.",
    "preco": 450.00,
    "quantidade": 20,
    "imagem": "https://images.unsplash.com/photo-1587829741301-dc798b83add3",
    "url": "teclado-mecanico-rgb"
  }
  ```
* **Retorno Esperado (201 Created):** Retorna o produto cadastrado.

### 🔹 PUT - Atualizar Produto (Apenas ADMIN)
* **URL:** `{{base_url}}/admin/produtos/api/{id}`
* **Headers:** `Authorization: Bearer <JWT_DO_ADMIN>` e `Content-Type: application/json`
* **Corpo (JSON):**
  ```json
  {
    "nome": "Teclado Mecânico RGB V2",
    "descricao": "Teclado mecânico atualizado com switch brown hot-swappable.",
    "preco": 489.90,
    "quantidade": 15,
    "imagem": "https://images.unsplash.com/photo-1587829741301-dc798b83add3",
    "url": "teclado-mecanico-rgb-v2"
  }
  ```

### 🔹 DELETE - Excluir Produto (Apenas ADMIN)
* **URL:** `{{base_url}}/admin/produtos/api/{id}`

---

## 💳 4. Pedidos e Checkout (`/api/pedidos`)

> [!NOTE]
> Requer cabeçalho `Authorization: Bearer <JWT>` do cliente.

### 🔹 POST - Finalizar Compra (Checkout)
* **URL:** `{{base_url}}/api/pedidos/checkout`
* **Retorno Esperado (200 OK):** Processa o carrinho atual do usuário logado, reduz as quantidades no estoque e gera o pedido faturado.
  ```json
  {
    "id": "7e000000-0000-0000-0000-000000000001",
    "dataPedido": "2026-06-22T15:20:00",
    "valorTotal": 15998.00,
    "status": "FINALIZADO",
    "itens": [
      {
        "id": "8e000000-0000-0000-0000-000000000001",
        "produtoId": "2e000000-0000-0000-0000-000000000001",
        "produtoNome": "Smartphone Galaxy S24 Ultra",
        "quantidade": 2,
        "precoUnitario": 7999.00,
        "precoTotal": 15998.00
      }
    ]
  }
  ```

### 🔹 GET - Listar Histórico de Pedidos
* **URL:** `{{base_url}}/api/pedidos`

---

## 🛡️ 5. Gerenciamento e Auditoria (Apenas ADMIN)

### 🔹 GET - Listar Logs de Auditoria
* **URL:** `{{base_url}}/admin/relatorios/api`
* **Headers:** `Authorization: Bearer <JWT_DO_ADMIN>`

### 🔹 GET - Listar Usuários
* **URL:** `{{base_url}}/admin/usuarios/api`
* **Headers:** `Authorization: Bearer <JWT_DO_ADMIN>`
