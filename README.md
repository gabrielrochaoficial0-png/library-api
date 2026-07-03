# 📚 Library API

API REST para gerenciamento de uma biblioteca, desenvolvida com **Java 17** e **Spring Boot 3**. Permite cadastrar livros e membros, além de controlar empréstimos e devoluções com validação de regras de negócio.

## 🚀 Tecnologias

- Java 17
- Spring Boot 3.2
- Spring Data JPA
- Spring Validation
- H2 Database (em memória)
- Lombok
- Springdoc OpenAPI (Swagger)
- JUnit 5 + Mockito

## 🧱 Arquitetura

O projeto segue a arquitetura em camadas:

```
Controller  →  Service  →  Repository  →  Database
   ↑              ↑
  DTOs      Regras de negócio
```

- **Controller**: expõe os endpoints REST e faz a validação de entrada.
- **Service**: concentra as regras de negócio (ex: não permitir empréstimo sem cópia disponível).
- **Repository**: acesso a dados via Spring Data JPA.
- **DTOs**: separam o modelo de persistência do modelo exposto na API.
- **Exception Handler**: tratamento centralizado de erros com respostas padronizadas.

## 📦 Domínio

| Entidade | Descrição |
|---|---|
| `Book` | Livro do acervo, com controle de cópias totais e disponíveis |
| `Member` | Usuário cadastrado na biblioteca |
| `Loan` | Empréstimo, relacionando um livro a um membro, com prazo de devolução |

### Regras de negócio implementadas

- Não é possível cadastrar um livro com ISBN já existente.
- Não é possível cadastrar um membro com e-mail já existente.
- Um empréstimo só pode ser feito se houver cópia disponível do livro.
- Ao emprestar, o número de cópias disponíveis é decrementado; ao devolver, incrementado.
- Não é possível devolver um empréstimo já finalizado.
- Não é possível excluir um livro com empréstimos ativos.
- Empréstimos com prazo vencido e ainda não devolvidos são sinalizados com status `ATRASADO`.

## ▶️ Como executar

Pré-requisitos: **Java 17+** e **Maven**.

```bash
git clone https://github.com/seu-usuario/library-api.git
cd library-api
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## 📖 Documentação da API (Swagger)

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

## 🗄️ Console do banco H2

```
http://localhost:8080/h2-console
```
- JDBC URL: `jdbc:h2:mem:librarydb`
- Usuário: `sa`
- Senha: *(em branco)*

## 🔌 Principais endpoints

### Livros
| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/books` | Cadastrar livro |
| GET | `/api/books` | Listar livros |
| GET | `/api/books/{id}` | Buscar livro por id |
| PUT | `/api/books/{id}` | Atualizar livro |
| DELETE | `/api/books/{id}` | Remover livro |

### Membros
| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/members` | Cadastrar membro |
| GET | `/api/members` | Listar membros |
| GET | `/api/members/{id}` | Buscar membro por id |
| PUT | `/api/members/{id}` | Atualizar membro |
| DELETE | `/api/members/{id}` | Remover membro |

### Empréstimos
| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/loans` | Registrar empréstimo |
| PATCH | `/api/loans/{id}/return` | Registrar devolução |
| GET | `/api/loans` | Listar empréstimos |
| GET | `/api/loans/{id}` | Buscar empréstimo por id |
| GET | `/api/loans/member/{memberId}` | Listar empréstimos de um membro |

### Exemplo de requisição — cadastrar livro

```json
POST /api/books
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "totalCopies": 3
}
```

### Exemplo de requisição — realizar empréstimo

```json
POST /api/loans
{
  "bookId": 1,
  "memberId": 1
}
```

## ✅ Testes

O projeto conta com testes unitários das principais regras de negócio, usando JUnit 5 e Mockito:

```bash
mvn test
```

## 📌 Possíveis evoluções

- Autenticação e autorização (Spring Security + JWT)
- Paginação e filtros nas listagens
- Migração de schema com Flyway
- Deploy com Docker

---

Projeto desenvolvido como parte dos estudos para o processo seletivo do **Programa Start by Capgemini**.
