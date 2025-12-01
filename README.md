# API de Usuários em Spring Boot

Uma API RESTful simples para cadastro de usuários (CRUD), desenvolvida com Spring Boot como parte de um estudo inicial.

> 🚧 **Status:** Em Desenvolvimento 🚧

---

## 💻 Tecnologias Utilizadas

Este projeto foi construído utilizando as seguintes tecnologias:

* **Java 21**
* **Spring Boot** (v3.5.7)
* **Maven** (Gerenciador de dependências)
* **Spring Web**: Para criação de endpoints RESTful.
* **Spring Data JPA**: Para persistência de dados.
* **MySQL**: Banco de dados para desenvolvimento e testes.
* **Lombok**: Para reduzir código boilerplate (getters, setters, construtores).

---

## 🚀 Como Executar o Projeto

Siga os passos abaixo para rodar a aplicação localmente.

### Pré-requisitos

* Java JDK 17 (ou superior) instalado.
* Maven instalado (ou utilize o Maven Wrapper `mvnw`).
* **Servidor MySQL** instalado e rodando (localmente ou acessível pela rede).
* Um "schema" (banco de dados) criado no MySQL (o nome deve ser o mesmo configurado no `application.properties`).
* Um cliente de API (como Postman ou Insomnia) para testar.

### Passos

1.  **Clone o repositório:**
    ```bash
    git clone [URL_DO_SEU_REPOSITORIO_AQUI]
    cd minha-api
    ```

2.  **Verifique a configuração do Banco de Dados:**
    * Confirme se os dados no arquivo `src/main/resources/application.properties` (`url`, `username`, `password`) estão corretos para a sua instalação local do MySQL.

3.  **Execute a aplicação (via Maven):**
    ```bash
    ./mvnw spring-boot:run
    ```

4.  **Alternativa (via IDE):**
    * Importe o projeto como um projeto Maven na sua IDE.
    * Encontre a classe principal `MinhaApiApplication.java`.
    * Clique com o botão direito e selecione "Run".

A aplicação estará disponível em `http://localhost:8080`.

---

## Endpoints da API

A URL base para todos os endpoints é `http://localhost:8080/api/usuarios`.

| Método | Endpoint | Descrição | Exemplo de Body (JSON) |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | Cria um novo usuário | `{ "nome", "senha"}` |
| `GET` | `/` | Lista todos os produtos cadastrados | N/A |
| `GET` | `/{id}` | Busca um produto específico pelo ID | N/A |

*(Endpoints de `PUT` e `DELETE` a serem implementados)*
