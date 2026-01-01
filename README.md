# 📘 Stay Agenda API (Backend)

API RESTful desenvolvida em Java com Spring Boot para gestão de clínicas de estética e barbearias. Responsável por toda a regra de negócio, segurança, persistência de dados e cálculos financeiros avançados do sistema Stay Agenda.

-----

## 🛠️ Tecnologias & Ferramentas

  * **Linguagem:** Java 21 (JDK 21)
  * **Framework:** Spring Boot 3.3.0
  * **Gerenciador de Dependências:** Maven
  * **Banco de Dados:** MySQL 8 (Produção via Aiven Cloud)
  * **ORM:** Spring Data JPA / Hibernate
  * **Segurança:** Spring Security 6 + JWT (Auth0 java-jwt)
  * **Containerização:** Docker
  * **Deploy:** Render.com

-----

## 🏛️ Arquitetura e Segurança

### Autenticação (JWT)

O sistema utiliza autenticação **Stateless** via JSON Web Tokens.

1.  **Login:** O usuário envia credenciais para `/api/auth/login`.
2.  **Token:** A API retorna um token JWT assinado (validade de 2 horas).
3.  **Acesso:** O token deve ser enviado no Header `Authorization: Bearer <token>` em todas as requisições protegidas.

### Configuração de CORS

A API está configurada para permitir requisições de **qualquer origem** (`*`) durante a fase de piloto, facilitando o acesso via IP local (Mobile) e localhost.

  * **Configuração:** `SecurityConfigurations.java` (Bean `corsConfigurationSource`).

-----

## 🧠 Regras de Negócio Financeiro (NOVO)

O diferencial do Stay Agenda é a precisão contábil no tratamento de pagamentos fracionados.

### 1. Lógica de Sinal + Restante 💰
O sistema processa entradas financeiras em duas etapas distintas para garantir a precisão das taxas:
* **Sinal (Entrada):** Processado via entidade `Agendamentos`. Permite uma forma de pagamento específica (ex: Pix sem taxa).
* **Restante (Quitação):** Processado via entidade `Pagamentos`. Permite outra forma de pagamento (ex: Crédito com taxa).
* **Cálculo Real:** O `FinanceiroService` unifica essas duas fontes, aplicando as taxas de maquininha individualmente apenas sobre a parcela que sofreu a cobrança.

### 2. Gestão de Despesas Inteligente 📉
* **Controller Unificado:** O endpoint `/api/despesas` possui inteligência para decidir a estratégia de busca (Listagem completa vs Filtragem por Período) com base nos parâmetros recebidos.
* **Importação NFC-e:** Serviço preparado para ler URLs de Notas Fiscais via QR Code e persistir os dados automaticamente.

-----

## 🗄️ Modelo de Dados (Entidades)

O banco de dados foi modelado para garantir integridade financeira e rastreabilidade.

### 1\. Núcleo

  * **`UsuariosModel` (`usuarios`)**: Acesso ao sistema (Login, Senha BCrypt, Perfil).
  * **`EmpresaModel` (`configuracao_empresa`)**: Armazena a identidade do tenant (Nome e Logo em BLOB) para personalização White Label.

### 2\. Cadastros

  * **`ClientesModel`**: Dados pessoais e histórico.
  * **`CategoriasModel`**: Agrupamento de procedimentos.
  * **`ProcedimentosModel`**: Serviços e preços base.

### 3\. Financeiro

  * **`PromocoesModel`**: Regras de desconto (Fixo ou %). Pode ser global ou específica por procedimento.
  * **`FormasPagamentoModel`**: Cadastro de meios (Pix, Cartão) com taxa administrativa (%) e flag de repasse ao cliente.
  * **`PagamentosModel`**: Registra a quitação (restante) vinculada a um agendamento. Grava o snapshot da taxa aplicada no momento.
  * **`DespesasModel`**: Registra saídas financeiras.
      * Campos de controle: `fornecedor`, `dataVencimento`, `pago` (status) e vínculo com categoria.

### 4\. Operacional

  * **`AgendamentosModel`**: Entidade central.
      * Armazena Data, Hora e Status.
      * **Snapshot Financeiro:** Grava `valorProcedimento` (total), `valorDesconto` e `valorParcial` (sinal) para evitar alterações retroativas se os preços mudarem.
      * **Taxas do Sinal:** Armazena especificamente a taxa aplicada sobre o valor da entrada antecipada.
  * **`MapeamentosModel`**: Histórico visual (Mídia).
      * Armazena fotos/vídeos em `LONGBLOB`.
      * Vincula mídia ao Cliente, Procedimento e Agendamento específico.

-----

## 🚀 Como Rodar Localmente

### Pré-requisitos

  * Java JDK 21 instalado.
  * Maven instalado (ou use o `mvnw` incluso).
  * MySQL rodando localmente (ou acesso ao banco na nuvem).

### 1\. Configurar Banco de Dados

Abra o arquivo `src/main/resources/application.properties` e configure suas credenciais:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stay_agenda
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

```

### 2. Executar a Aplicação

No terminal, na raiz do projeto:

```bash
./mvnw spring-boot:run

```

A API estará disponível em: `http://localhost:8080`

---

## ☁️ Deploy (Render + Docker)

O projeto inclui um `Dockerfile` configurado para *Multi-stage build*, otimizado para o Render.

### Variáveis de Ambiente (Environment Variables)

Para rodar em produção, configure as seguintes variáveis no painel do Render:

| Variável | Descrição | Exemplo |
| --- | --- | --- |
| `DB_URL` | URL JDBC do Banco (Aiven/TiDB) | `jdbc:mysql://host:port/db?ssl-mode=REQUIRED` |
| `DB_USER` | Usuário do Banco | `avnadmin` |
| `DB_PASSWORD` | Senha do Banco | `s3nh4-f0rt3` |
| `JWT_SECRET` | Chave privada para assinatura | `minha-chave-secreta-jwt` |
| `PORT` | Porta da aplicação | `8080` |

---

## 📦 Endpoints Principais

### Autenticação

* `POST /api/auth/login`: Autenticar e receber Token.

### Financeiro (Novo)

* `GET /api/financeiro/extrato`: Retorna fluxo de caixa unificado (Sinais + Pagamentos - Despesas).
* `GET /api/despesas`: Lista despesas (com suporte a filtro `?inicio=YYYY-MM-DD&fim=YYYY-MM-DD`).
* `POST /api/despesas/importar/qrcode`: Importa despesa via URL de Nota Fiscal.

### Agendamentos

* `GET /api/agendamentos/filtro?data=YYYY-MM-DD`: Busca agenda do dia (usado no Dashboard).
* `POST /api/agendamentos`: Cria novo agendamento (calcula descontos e taxas).
* `PUT /api/agendamentos/{id}`: Atualiza dados e recalcula financeiro se necessário.

### Mapeamentos (Mídia)

* `POST /api/mapeamentos`: Upload de Foto/Vídeo (Multipart File).
* `GET /api/mapeamentos/cliente/{id}`: Retorna histórico visual do cliente.

### Configurações

* `GET /api/empresa`: Retorna dados públicos da empresa (Logo/Nome) para a tela de login.
* `POST /api/empresa`: Atualiza identidade visual.