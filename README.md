
# SkillBridge.AI — API (Spring Boot 3 / Java 24)

API REST simples para gestão de **Usuários**, **Trilhas** e **Matrículas**.  
Stack: Spring Boot (Web, Validation, Data JPA, Security DEV), H2, Flyway, Swagger (springdoc 2.7.0).

---

## 🚀 Como rodar (DEV)

**Requisitos:** JDK 24, Maven 3.9+

```bash
mvn spring-boot:run
````

* App: [http://localhost:8080](http://localhost:8080)
* Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
* H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

  * JDBC: `jdbc:h2:mem:skillbridge`
  * user: `sa` / senha: *vazio*

> As migrações rodam automaticamente (Flyway). O schema inicial está em `src/main/resources/db/migration/V1__init.sql`.

---

## 🧱 Domínio & Regras

* **Usuário**: `nome` obrigatório; `email` válido/único; **`cpf` 11 dígitos/único**.
* **Trilha**: `titulo` obrigatório; `descricao` opcional; `ativa` (default: `true`).
* **Matrícula**: unicidade `(user_id, trilha_id)` — um usuário não pode se matricular duas vezes na mesma trilha.

---

## 🔌 Endpoints

### Usuários

| Método | Rota                    | Corpo (JSON)                                              | Retorno    |
| -----: | ----------------------- | --------------------------------------------------------- | ---------- |
|   POST | `/api/v1/usuarios`      | `{ "nome": "...", "email": "...", "cpf": "12345678901" }` | 201 User   |
|    GET | `/api/v1/usuarios`      | —                                                         | 200 [User] |
|    GET | `/api/v1/usuarios/{id}` | —                                                         | 200 User   |
|    PUT | `/api/v1/usuarios/{id}` | `{ "nome": "...", "email": "...", "cpf": "..." }`         | 200 User   |
| DELETE | `/api/v1/usuarios/{id}` | —                                                         | 204        |

### Trilhas

| Método | Rota                   | Corpo (JSON)                                             | Retorno      |
| -----: | ---------------------- | -------------------------------------------------------- | ------------ |
|   POST | `/api/v1/trilhas`      | `{ "titulo": "...", "descricao": "...", "ativa": true }` | 201 Trilha   |
|    GET | `/api/v1/trilhas`      | —                                                        | 200 [Trilha] |
|    GET | `/api/v1/trilhas/{id}` | —                                                        | 200 Trilha   |
|    PUT | `/api/v1/trilhas/{id}` | `{ "titulo": "...", "descricao": "...", "ativa": true }` | 200 Trilha   |
| DELETE | `/api/v1/trilhas/{id}` | —                                                        | 204          |

### Matrículas

| Método | Rota                      | Corpo / Querystring                        | Retorno         |
| -----: | ------------------------- | ------------------------------------------ | --------------- |
|   POST | `/api/v1/matriculas`      | `{ "userId": 1, "trilhaId": 1 }`           | 201 Matrícula   |
|    GET | `/api/v1/matriculas`      | `?userId=1` e/ou `?trilhaId=1` (opcionais) | 200 [Matrícula] |
| DELETE | `/api/v1/matriculas/{id}` | —                                          | 204             |

---

## 🧪 Requests de exemplo

### PowerShell (Windows)

```powershell
# Criar trilha
$body = @{ titulo="Java & DDD"; descricao="Fundamentos"; ativa=$true } | ConvertTo-Json
Invoke-RestMethod http://localhost:8080/api/v1/trilhas -Method Post -ContentType "application/json" -Body $body

# Criar usuário
$body = @{ nome="Abner"; email="abner@fiap.com"; cpf="12345678901" } | ConvertTo-Json
Invoke-RestMethod http://localhost:8080/api/v1/usuarios -Method Post -ContentType "application/json" -Body $body

# Matricular
$body = @{ userId=1; trilhaId=1 } | ConvertTo-Json
Invoke-RestMethod http://localhost:8080/api/v1/matriculas -Method Post -ContentType "application/json" -Body $body
```

### CMD (Windows)

```bat
curl.exe -s -X POST http://localhost:8080/api/v1/trilhas -H "Content-Type: application/json" -d "{\"titulo\":\"Java & DDD\",\"descricao\":\"Fundamentos\",\"ativa\":true}"

curl.exe -s -X POST http://localhost:8080/api/v1/usuarios -H "Content-Type: application/json" -d "{\"nome\":\"Abner\",\"email\":\"abner@fiap.com\",\"cpf\":\"12345678901\"}"

curl.exe -s -X POST http://localhost:8080/api/v1/matriculas -H "Content-Type: application/json" -d "{\"userId\":1,\"trilhaId\":1}"
```

---

## Erros (payload padrão)

```json
{
  "timestamp": "2025-11-12T19:14:18.062945Z",
  "status": 400,
  "error": "Bad Request",
  "message": "E-mail já cadastrado.",
  "path": "/api/v1/usuarios",
  "fieldErrors": ["email: must be a well-formed email address"]
}
```

> Em DEV, o Security está aberto para Swagger/H2 e requisições públicas. **Não usar essa config em produção.**

---

## Estrutura (resumo)

```
src/main/java/br/com/fiap/skillbridge/ai
 ├─ config/                # Security / CORS (DEV)
 ├─ shared/exception/      # ApiError, NotFoundException, Handler
 ├─ user/                  # model, dto, repository, service, controller
 ├─ trilha/                # model, dto, repository, service, controller
 └─ matricula/             # model, dto, repository, service, controller

src/main/resources
 ├─ application.properties
 └─ db/migration/V1__init.sql
```

---

## Build & Test

```bash
# build
mvn -q clean package

# testes
mvn -q test
```

> Cobertura pode ser verificada pelo IntelliJ (Run with Coverage).
> Para Jacoco (opcional), adicione o plugin e gere `target/site/jacoco/index.html`.

---

## Equipe

* Abner de Paiva Barbosa - RM558468
* Fernando Luiz Silva Antonio - RM555201
* Thomas de Almeida Reichmann - RM554812
