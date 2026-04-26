# Aegis B2B Identity

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)

**Live API:** https://aegis-api-f5e1.onrender.com  
**Documentation (Swagger):** https://aegis-api-f5e1.onrender.com/swagger-ui/index.html

---

*Leia em [Português](#versão-em-português)*

---

## What is this?

Aegis B2B Identity is an Identity and Access Management (IAM) engine built for corporate environments. It handles everything related to who can log in, what they can access, and how sessions are managed — following the same standards as tools like Keycloak and Auth0.

---

## Problems this project solves

**XSS vulnerability in token storage**  
Most APIs return both the Access Token and the Refresh Token in the JSON response, which means client-side scripts can read and steal them. Aegis splits the delivery: the Access Token goes in the JSON body, and the Refresh Token goes in an HttpOnly Secure Cookie, completely invisible to JavaScript. This eliminates the most common token theft vector.

**Data leaking between companies in multi-tenant systems**  
When a single system serves multiple companies, one wrong query can expose data from the wrong tenant. Aegis solves this by injecting the Tenant ID directly into the security context on every request, so database sessions are automatically isolated — there's no way for Company A to accidentally read Company B's data.

**No safe way to invalidate sessions immediately**  
JWTs are stateless by nature, which means once issued, they stay valid until they expire — even after logout. Aegis uses Redis as a token blacklist, so any token can be invalidated the moment the user logs out or when a security event is detected, without waiting for expiration.

---

## How the authentication flow works

| Step | What happens |
|------|-------------|
| 1. Login | User sends email and password to `/auth/login`. The password is validated using BCrypt. |
| 2. Token generation | An Access Token (short-lived JWT signed with a 2048-bit RSA private key) and an Opaque Refresh Token (valid for 7 days) are generated. |
| 3. Secure delivery | The Access Token is returned in the JSON body. The Refresh Token is sent via an HttpOnly + Secure Cookie, unreachable by client-side scripts. |
| 4. Request interception | On every protected request, a filter validates the JWT signature using the RSA public key and extracts the Tenant ID to isolate the database session. |
| 5. Token rotation | When the Access Token expires, the client hits `/auth/refresh`. The system reads the secure cookie, invalidates the old Refresh Token in Redis, and issues a completely new pair. |

---

## Tech stack

| Technology | Why it's here |
|------------|--------------|
| Kotlin 1.9+ | Coroutines and strong null safety reduce runtime errors significantly |
| Spring Boot 3.x + Spring Security 6 | Industry standard for corporate-grade backends |
| PostgreSQL | Relational data with full multi-tenant isolation |
| Redis | Token blacklisting for immediate logout and session control |
| Docker & Docker Compose | Full containerization for consistent environments |
| Render | Managed cloud deployment |

---

## Running locally

**Prerequisites:** Java 17+, Docker, Docker Compose

```bash
# Clone the repository
git clone https://github.com/your-username/aegis-b2b-identity.git
cd aegis-b2b-identity

# Start infrastructure (PostgreSQL + Redis)
docker-compose up -d

# Run the application
./gradlew bootRun
```

**Required environment variables:**

```env
DB_URL=jdbc:postgresql://localhost:5432/aegis
DB_USERNAME=your_user
DB_PASSWORD=your_password

REDIS_HOST=localhost
REDIS_PORT=6379

RSA_PRIVATE_KEY=...
RSA_PUBLIC_KEY=...
```

---

## API endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/login` | Authenticate with email and password |
| POST | `/auth/refresh` | Rotate the token pair using the secure cookie |
| POST | `/auth/logout` | Immediately invalidate the session via Redis |

Full interactive documentation available at the Swagger link above.

---
---

# Versão em Português

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)

**API em produção:** https://aegis-api-f5e1.onrender.com  
**Documentação (Swagger):** https://aegis-api-f5e1.onrender.com/swagger-ui/index.html

---

## O que é isso?

Aegis B2B Identity é um motor de Gestão de Identidade e Acessos (IAM) feito para ambientes corporativos. Ele cuida de tudo relacionado a quem pode fazer login, o que cada pessoa pode acessar e como as sessões são gerenciadas — seguindo os mesmos padrões de ferramentas como Keycloak e Auth0.

---

## Problemas que esse projeto resolve

**Vulnerabilidade de XSS no armazenamento de tokens**  
A maioria das APIs devolve o Access Token e o Refresh Token juntos no corpo do JSON, o que significa que qualquer script no navegador consegue lê-los e roubá-los. O Aegis separa a entrega: o Access Token vai no JSON, e o Refresh Token vai em um Cookie HttpOnly e Secure, completamente inacessível para JavaScript. Isso elimina o vetor de roubo de tokens mais comum.

**Dados de empresas diferentes vazando entre si em sistemas multi-tenant**  
Quando um sistema atende várias empresas ao mesmo tempo, uma query errada pode expor dados do tenant errado. O Aegis resolve isso injetando o Tenant ID diretamente no contexto de segurança em cada requisição, isolando automaticamente a sessão do banco de dados — não existe caminho para a Empresa A ler os dados da Empresa B por acidente.

**Sem forma segura de invalidar sessões imediatamente**  
JWTs são stateless por natureza, o que significa que, uma vez emitidos, ficam válidos até expirar — mesmo depois do logout. O Aegis usa o Redis como uma blacklist de tokens, então qualquer token pode ser invalidado no momento exato em que o usuário faz logout ou quando um evento de segurança é detectado, sem precisar esperar a expiração.

---

## Como o fluxo de autenticação funciona

| Etapa | O que acontece |
|-------|---------------|
| 1. Login | O usuário envia email e senha para `/auth/login`. A senha é validada via BCrypt. |
| 2. Geração de tokens | Um Access Token (JWT de curta duração assinado com chave RSA privada de 2048 bits) e um Refresh Token opaco (válido por 7 dias) são gerados. |
| 3. Entrega segura | O Access Token é devolvido no corpo do JSON. O Refresh Token é enviado via Cookie HttpOnly + Secure, inacessível para scripts no browser. |
| 4. Interceptação de requisições | Em cada requisição protegida, um filtro valida a assinatura do JWT usando a chave RSA pública e extrai o Tenant ID para isolar a sessão do banco. |
| 5. Rotação de tokens | Quando o Access Token expira, o cliente chama `/auth/refresh`. O sistema lê o cookie seguro, invalida o Refresh Token antigo no Redis e emite um par completamente novo. |

---

## Stack de tecnologias

| Tecnologia | Por que está aqui |
|------------|------------------|
| Kotlin 1.9+ | Coroutines e null safety forte reduzem erros em runtime significativamente |
| Spring Boot 3.x + Spring Security 6 | Padrão da indústria para backends corporativos |
| PostgreSQL | Dados relacionais com isolamento multi-tenant completo |
| Redis | Blacklist de tokens para logout imediato e controle de sessões |
| Docker & Docker Compose | Containerização completa para ambientes consistentes |
| Render | Deploy em nuvem gerenciada |

---

## Rodando localmente

**Pré-requisitos:** Java 17+, Docker, Docker Compose

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/aegis-b2b-identity.git
cd aegis-b2b-identity

# Sobe a infraestrutura (PostgreSQL + Redis)
docker-compose up -d

# Roda a aplicação
./gradlew bootRun
```

**Variáveis de ambiente necessárias:**

```env
DB_URL=jdbc:postgresql://localhost:5432/aegis
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha

REDIS_HOST=localhost
REDIS_PORT=6379

RSA_PRIVATE_KEY=...
RSA_PUBLIC_KEY=...
```

---

## Endpoints da API

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/login` | Autenticação com email e senha |
| POST | `/auth/refresh` | Rotação do par de tokens via cookie seguro |
| POST | `/auth/logout` | Invalidação imediata da sessão via Redis |

Documentação interativa completa disponível no link do Swagger acima.
