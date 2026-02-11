# OAuth 2.0 Authorization Server

Servidor OAuth 2.0 completo con Spring Boot 3.2, Spring Security Authorization Server, PostgreSQL y Maven.

## 🚀 Características

- ✅ OAuth 2.0 Authorization Server
- ✅ OpenID Connect (OIDC)
- ✅ JWT Token Generation
- ✅ PostgreSQL Database
- ✅ User Management
- ✅ Client Credentials
- ✅ Authorization Code Flow
- ✅ Refresh Token

## 📋 Requisitos

- Java 17+
- Maven 3.6+
- Docker & Docker Compose (para PostgreSQL)
- PostgreSQL 15+ (o usar Docker)

## 🔧 Instalación

### 1. Clonar el repositorio o crear estructura de carpetas

```bash
mkdir oauth-server && cd oauth-server
```

### 2. Levantar PostgreSQL con Docker

```bash
docker-compose up -d
```

Esto creará:
- PostgreSQL en puerto 5432
- Base de datos: `oauth_db`
- Usuario: `postgres`
- Password: `postgres`

### 3. Compilar el proyecto

```bash
mvn clean install
```

### 4. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

O si tienes el JAR compilado:

```bash
java -jar target/oauth-server-1.0-SNAPSHOT.jar
```

El servidor estará disponible en: **http://localhost:9000**

## 👥 Usuarios de Prueba

Al iniciar, se crean automáticamente estos usuarios:

| Username | Password | Roles |
|----------|----------|-------|
| user     | password | USER  |
| admin    | admin    | USER, ADMIN |

## 🔐 Cliente OAuth Configurado

**Client ID**: `public-client`  
**Client Secret**: `secret`  
**Grant Types**: 
- Authorization Code
- Refresh Token
- Client Credentials

**Redirect URIs**:
- `http://127.0.0.1:8080/login/oauth2/code/public-client`
- `http://127.0.0.1:8080/authorized`

**Scopes**: `openid`, `profile`, `email`, `read`, `write`

## 📡 Endpoints Importantes

### Endpoints de OAuth 2.0

- **Authorization Endpoint**: `http://localhost:9000/oauth2/authorize`
- **Token Endpoint**: `http://localhost:9000/oauth2/token`
- **JWK Set**: `http://localhost:9000/oauth2/jwks`
- **OpenID Configuration**: `http://localhost:9000/.well-known/openid-configuration`
- **Revoke Token**: `http://localhost:9000/oauth2/revoke`
- **Introspect Token**: `http://localhost:9000/oauth2/introspect`

### Endpoints de API

- **Public**: `GET http://localhost:9000/api/public`
- **User**: `GET http://localhost:9000/api/user` (requiere autenticación)
- **Admin**: `GET http://localhost:9000/api/admin` (requiere autenticación)

## 🔄 Flujo de Authorization Code

### 1. Obtener código de autorización

Abre en el navegador:

```
http://localhost:9000/oauth2/authorize?
  response_type=code&
  client_id=public-client&
  scope=openid%20profile%20email%20read&
  redirect_uri=http://127.0.0.1:8080/authorized&
  state=xyz
```

Esto te pedirá:
1. Login (usar: `user` / `password`)
2. Consentimiento de scopes
3. Te redirigirá a: `http://127.0.0.1:8080/authorized?code=CODIGO&state=xyz`

### 2. Intercambiar código por token

```bash
curl -X POST http://localhost:9000/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u public-client:secret \
  -d "grant_type=authorization_code" \
  -d "code=CODIGO_OBTENIDO" \
  -d "redirect_uri=http://127.0.0.1:8080/authorized"
```

Respuesta:
```json
{
  "access_token": "eyJraWQiOiI...",
  "refresh_token": "RbYGP3t...",
  "scope": "openid profile email read",
  "id_token": "eyJraWQiOiI...",
  "token_type": "Bearer",
  "expires_in": 1799
}
```

### 3. Usar el Access Token

```bash
curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  http://localhost:9000/api/user
```

### 4. Refresh Token

```bash
curl -X POST http://localhost:9000/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u public-client:secret \
  -d "grant_type=refresh_token" \
  -d "refresh_token=YOUR_REFRESH_TOKEN"
```

## 🔑 Client Credentials Flow

Para aplicaciones máquina-a-máquina:

```bash
curl -X POST http://localhost:9000/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u public-client:secret \
  -d "grant_type=client_credentials" \
  -d "scope=read write"
```

## 🗄️ Estructura de la Base de Datos

```
users
  - id (PK)
  - username (unique)
  - password (encrypted)
  - email
  - enabled

user_roles
  - user_id (FK)
  - role
```

## ⚙️ Configuración

Edita `src/main/resources/application.yml` para cambiar:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/oauth_db
    username: postgres
    password: postgres

server:
  port: 9000
```

## 🧪 Testing

### Verificar que el servidor está funcionando

```bash
curl http://localhost:9000/.well-known/openid-configuration
```

### Obtener JWK Set

```bash
curl http://localhost:9000/oauth2/jwks
```

## 📦 Estructura del Proyecto

```
oauth-server/
├── src/
│   └── main/
│       ├── java/com/example/oauth/
│       │   ├── OAuthServerApplication.java
│       │   ├── config/
│       │   │   ├── AuthorizationServerConfig.java
│       │   │   └── DataLoader.java
│       │   ├── entity/
│       │   │   └── UserEntity.java
│       │   ├── repository/
│       │   │   └── UserRepository.java
│       │   ├── service/
│       │   │   ├── CustomUserDetailsService.java
│       │   │   └── UserService.java
│       │   └── controller/
│       │       └── TestController.java
│       └── resources/
│           └── application.yml
├── database/
│   └── init.sql
├── docker-compose.yml
└── pom.xml
```

## 🐛 Troubleshooting

### Error: Connection refused to PostgreSQL

```bash
# Verifica que PostgreSQL está corriendo
docker-compose ps

# Reinicia PostgreSQL
docker-compose restart postgres
```

### Error: Port 9000 already in use

Cambia el puerto en `application.yml`:
```yaml
server:
  port: 9001
```

### Ver logs de la aplicación

```bash
# Con nivel DEBUG de Spring Security
mvn spring-boot:run -Dlogging.level.org.springframework.security=DEBUG
```

## 📚 Recursos Adicionales

- [Spring Authorization Server Documentation](https://docs.spring.io/spring-authorization-server/reference/index.html)
- [OAuth 2.0 RFC 6749](https://datatracker.ietf.org/doc/html/rfc6749)
- [OpenID Connect Core](https://openid.net/specs/openid-connect-core-1_0.html)

## 🤝 Contribuir

1. Fork el proyecto
2. Crea tu feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la branch (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.

## ✨ Próximas Características

- [ ] Registro de usuarios via API
- [ ] Gestión de clientes OAuth via API
- [ ] Two-Factor Authentication (2FA)
- [ ] Social Login (Google, GitHub)
- [ ] Rate Limiting
- [ ] Auditoría de tokens
