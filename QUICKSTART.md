# 🚀 Quick Start Guide

## Inicio Rápido en 5 Pasos

### 1️⃣ Levantar PostgreSQL
```bash
docker-compose up -d
```

### 2️⃣ Compilar el proyecto
```bash
mvn clean install
```

### 3️⃣ Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### 4️⃣ Probar el servidor
Abre en el navegador:
```
http://localhost:9000/.well-known/openid-configuration
```

### 5️⃣ Obtener un token (Client Credentials)
```bash
curl -X POST http://localhost:9000/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u public-client:secret \
  -d "grant_type=client_credentials" \
  -d "scope=read write"
```

## 🔐 Flujo de Authorization Code (paso a paso)

### Paso 1: Obtener código de autorización
Abre en el navegador:
```
http://localhost:9000/oauth2/authorize?response_type=code&client_id=public-client&scope=openid%20profile%20email%20read&redirect_uri=http://127.0.0.1:8080/authorized&state=xyz
```

**Login:** user / password

Serás redirigido a:
```
http://127.0.0.1:8080/authorized?code=CODIGO_AQUI&state=xyz
```

### Paso 2: Copiar el código de la URL
Ejemplo: `code=abc123xyz`

### Paso 3: Intercambiar código por token
```bash
curl -X POST http://localhost:9000/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u public-client:secret \
  -d "grant_type=authorization_code" \
  -d "code=TU_CODIGO_AQUI" \
  -d "redirect_uri=http://127.0.0.1:8080/authorized"
```

### Paso 4: Usar el Access Token
```bash
curl -H "Authorization: Bearer TU_ACCESS_TOKEN" \
  http://localhost:9000/api/user
```

## 📊 Estructura de Respuesta del Token

```json
{
  "access_token": "eyJraWQiOiI5ZGQ4...",
  "refresh_token": "RbYGP3tKhDW...",
  "scope": "openid profile email read",
  "id_token": "eyJraWQiOiI5ZGQ4...",
  "token_type": "Bearer",
  "expires_in": 1799
}
```

## ✅ Verificación

### Verificar que PostgreSQL está corriendo
```bash
docker-compose ps
```

### Ver logs de PostgreSQL
```bash
docker-compose logs -f postgres
```

### Ver logs de la aplicación
```bash
mvn spring-boot:run -Dlogging.level.org.springframework.security=DEBUG
```

## 🛠️ Comandos Útiles

### Reiniciar todo
```bash
docker-compose down
docker-compose up -d
mvn clean install
mvn spring-boot:run
```

### Borrar base de datos y empezar de cero
```bash
docker-compose down -v
docker-compose up -d
```

### Compilar sin tests
```bash
mvn clean install -DskipTests
```

## 🔍 Testing con cURL

### 1. Client Credentials
```bash
curl -X POST http://localhost:9000/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u public-client:secret \
  -d "grant_type=client_credentials" \
  -d "scope=read"
```

### 2. Refresh Token
```bash
curl -X POST http://localhost:9000/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u public-client:secret \
  -d "grant_type=refresh_token" \
  -d "refresh_token=TU_REFRESH_TOKEN"
```

### 3. Introspect Token
```bash
curl -X POST http://localhost:9000/oauth2/introspect \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u public-client:secret \
  -d "token=TU_ACCESS_TOKEN"
```

### 4. Endpoint protegido
```bash
curl -H "Authorization: Bearer TU_ACCESS_TOKEN" \
  http://localhost:9000/api/user
```

## 📦 Importar en Postman

1. Abre Postman
2. Import → Upload Files
3. Selecciona `postman_collection.json`
4. Ya tienes todos los endpoints configurados

## 🎯 Credenciales por Defecto

**Usuarios:**
- Username: `user` | Password: `password` | Role: USER
- Username: `admin` | Password: `admin` | Role: ADMIN, USER

**Cliente OAuth:**
- Client ID: `public-client`
- Client Secret: `secret`

**Base de Datos:**
- Host: `localhost:5432`
- Database: `oauth_db`
- Username: `postgres`
- Password: `postgres`

## ❓ Problemas Comunes

### Puerto 9000 ocupado
Cambia en `application.yml`:
```yaml
server:
  port: 9001
```

### No conecta a PostgreSQL
```bash
docker-compose restart postgres
```

### Error de compilación
```bash
mvn clean
mvn install -U
```

## 🎉 ¡Listo!

Tu servidor OAuth 2.0 está funcionando en:
**http://localhost:9000**

Documentación completa en: `README.md`
