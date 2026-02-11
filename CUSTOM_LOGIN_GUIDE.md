# 🎨 Login Personalizado - Guía de Integración

Esta guía te muestra cómo usar tu **propio diseño de login** en lugar del formulario por defecto de Spring Security.

## 📋 Tabla de Contenidos

1. [Cómo Funciona](#cómo-funciona)
2. [Endpoints de API](#endpoints-de-api)
3. [Integración Frontend](#integración-frontend)
4. [Ejemplos de Código](#ejemplos-de-código)
5. [Configuración CORS](#configuración-cors)
6. [Flujos de Autenticación](#flujos-de-autenticación)

---

## 🔄 Cómo Funciona

### Arquitectura

```
┌─────────────────────┐
│   Tu Aplicación     │
│   (Frontend)        │
│                     │
│  ┌───────────────┐  │
│  │ Login Custom  │  │
│  │   (HTML/JS)   │  │
│  └───────┬───────┘  │
│          │          │
└──────────┼──────────┘
           │ POST /api/auth/login
           │ { username, password }
           ▼
┌─────────────────────┐
│  OAuth Server       │
│  (Backend)          │
│                     │
│  ┌───────────────┐  │
│  │ AuthController│──┼─► Valida credenciales
│  └───────────────┘  │
│          │          │
│          ▼          │
│  ┌───────────────┐  │
│  │   Database    │  │
│  └───────────────┘  │
└──────────┬──────────┘
           │ Response:
           │ { username, roles, authenticated }
           ▼
```

### Flujo Básico

1. **Usuario ingresa credenciales** en tu formulario custom
2. **Frontend envía POST** a `/api/auth/login`
3. **Servidor valida** usuario y contraseña
4. **Retorna respuesta** con datos del usuario
5. **Frontend guarda sesión** (localStorage, cookie, etc.)

---

## 🔌 Endpoints de API

### 1. Login (Autenticación)

**Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "username": "user",
  "password": "password"
}
```

**Response (Success - 200):**
```json
{
  "username": "user",
  "roles": "ROLE_USER",
  "message": "Login exitoso",
  "authenticated": true
}
```

**Response (Error - 401):**
```json
{
  "error": "Credenciales inválidas",
  "message": "Usuario o contraseña incorrectos"
}
```

### 2. Obtener Usuario Actual

**Endpoint:** `GET /api/auth/me`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "username": "user",
  "authorities": ["ROLE_USER"],
  "authenticated": true
}
```

### 3. Logout

**Endpoint:** `POST /api/auth/logout`

**Response:**
```json
{
  "message": "Sesión cerrada exitosamente",
  "success": true
}
```

### 4. Obtener Token OAuth (Opcional)

**Endpoint:** `POST /api/auth/token`

**Request:**
```json
{
  "username": "user",
  "password": "password"
}
```

---

## 💻 Integración Frontend

### Opción 1: HTML + JavaScript Vanilla

```html
<!DOCTYPE html>
<html>
<body>
    <form id="loginForm">
        <input type="text" id="username" placeholder="Usuario">
        <input type="password" id="password" placeholder="Contraseña">
        <button type="submit">Iniciar Sesión</button>
    </form>

    <script>
        document.getElementById('loginForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const response = await fetch('http://localhost:9000/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: document.getElementById('username').value,
                    password: document.getElementById('password').value
                })
            });
            
            const data = await response.json();
            
            if (response.ok) {
                console.log('Login exitoso:', data);
                localStorage.setItem('user', JSON.stringify(data));
                // Redirigir o actualizar UI
            } else {
                console.error('Error:', data.message);
            }
        });
    </script>
</body>
</html>
```

### Opción 2: React

```jsx
import { useState } from 'react';

function Login() {
  const [credentials, setCredentials] = useState({ username: '', password: '' });

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const response = await fetch('http://localhost:9000/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials)
    });
    
    const data = await response.json();
    
    if (response.ok) {
      localStorage.setItem('user', JSON.stringify(data));
      // Redirigir o actualizar estado
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input 
        type="text"
        value={credentials.username}
        onChange={(e) => setCredentials({...credentials, username: e.target.value})}
      />
      <input 
        type="password"
        value={credentials.password}
        onChange={(e) => setCredentials({...credentials, password: e.target.value})}
      />
      <button type="submit">Login</button>
    </form>
  );
}
```

### Opción 3: Angular

```typescript
import { HttpClient } from '@angular/common/http';

export class LoginComponent {
  constructor(private http: HttpClient) {}

  login(username: string, password: string) {
    this.http.post('http://localhost:9000/api/auth/login', {
      username,
      password
    }).subscribe({
      next: (response: any) => {
        localStorage.setItem('user', JSON.stringify(response));
        // Redirigir
      },
      error: (error) => {
        console.error('Error de login:', error);
      }
    });
  }
}
```

### Opción 4: Vue.js

```vue
<template>
  <form @submit.prevent="handleLogin">
    <input v-model="username" type="text" placeholder="Usuario">
    <input v-model="password" type="password" placeholder="Contraseña">
    <button type="submit">Login</button>
  </form>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: ''
    }
  },
  methods: {
    async handleLogin() {
      const response = await fetch('http://localhost:9000/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: this.username,
          password: this.password
        })
      });
      
      const data = await response.json();
      
      if (response.ok) {
        localStorage.setItem('user', JSON.stringify(data));
        this.$router.push('/dashboard');
      }
    }
  }
}
</script>
```

---

## 🌐 Configuración CORS

Para que tu frontend pueda comunicarse con el servidor OAuth desde un dominio diferente:

### Opción 1: Configuración Global

Edita `AuthorizationServerConfig.java`:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:4200") // Tus frontends
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### Opción 2: Configuración en Controller

Ya está configurado en `AuthenticationController.java`:

```java
@CrossOrigin(origins = "*") // Cambiar "*" por tus dominios en producción
```

---

## 🔐 Flujos de Autenticación

### Flujo 1: Login Simple (Session-based)

```javascript
// 1. Login
const loginResponse = await fetch('/api/auth/login', {
  method: 'POST',
  body: JSON.stringify({ username, password })
});

// 2. Guardar sesión
localStorage.setItem('user', JSON.stringify(loginResponse));

// 3. Usar en requests
const user = JSON.parse(localStorage.getItem('user'));
if (user && user.authenticated) {
  // Usuario autenticado
}
```

### Flujo 2: OAuth 2.0 Authorization Code

```javascript
// 1. Redirigir a autorización
window.location.href = 'http://localhost:9000/oauth2/authorize?' +
  'response_type=code&' +
  'client_id=public-client&' +
  'redirect_uri=http://localhost:3000/callback&' +
  'scope=openid profile email';

// 2. En la página de callback, intercambiar código por token
const code = new URLSearchParams(window.location.search).get('code');

const tokenResponse = await fetch('http://localhost:9000/oauth2/token', {
  method: 'POST',
  headers: {
    'Authorization': 'Basic ' + btoa('public-client:secret'),
    'Content-Type': 'application/x-www-form-urlencoded'
  },
  body: new URLSearchParams({
    grant_type: 'authorization_code',
    code: code,
    redirect_uri: 'http://localhost:3000/callback'
  })
});

const tokens = await tokenResponse.json();
// tokens.access_token, tokens.refresh_token, etc.
```

### Flujo 3: Client Credentials (App-to-App)

```javascript
const tokenResponse = await fetch('http://localhost:9000/oauth2/token', {
  method: 'POST',
  headers: {
    'Authorization': 'Basic ' + btoa('public-client:secret'),
    'Content-Type': 'application/x-www-form-urlencoded'
  },
  body: new URLSearchParams({
    grant_type: 'client_credentials',
    scope: 'read write'
  })
});
```

---

## 🧪 Testing

### Con cURL

```bash
# Login
curl -X POST http://localhost:9000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'

# Get current user (requiere sesión)
curl http://localhost:9000/api/auth/me \
  -H "Cookie: JSESSIONID=YOUR_SESSION_ID"

# Logout
curl -X POST http://localhost:9000/api/auth/logout
```

### Con Postman

1. Importa `postman_collection.json`
2. Usa el request "Login - Custom API"
3. Las credenciales de prueba son:
   - `user` / `password`
   - `admin` / `admin`

---

## ⚙️ Configuración Avanzada

### Guardar Token en HTTP-Only Cookie

Para mayor seguridad, puedes modificar el `AuthenticationController` para guardar el token en una cookie HTTP-Only:

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request, 
                               HttpServletResponse response) {
    // ... autenticación ...
    
    // Crear cookie HTTP-Only
    Cookie cookie = new Cookie("AUTH_TOKEN", sessionId);
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // Solo HTTPS en producción
    cookie.setPath("/");
    cookie.setMaxAge(24 * 60 * 60); // 24 horas
    
    response.addCookie(cookie);
    
    return ResponseEntity.ok(loginResponse);
}
```

### Refresh Token Automático

```javascript
async function refreshToken() {
  const response = await fetch('/oauth2/token', {
    method: 'POST',
    headers: {
      'Authorization': 'Basic ' + btoa('public-client:secret'),
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: new URLSearchParams({
      grant_type: 'refresh_token',
      refresh_token: localStorage.getItem('refresh_token')
    })
  });
  
  const tokens = await response.json();
  localStorage.setItem('access_token', tokens.access_token);
}
```

---

## ✅ Checklist de Implementación

- [ ] Backend OAuth Server corriendo en puerto 9000
- [ ] Endpoints `/api/auth/login` accesibles
- [ ] CORS configurado para tu frontend
- [ ] Frontend puede hacer POST a `/api/auth/login`
- [ ] Respuestas JSON siendo parseadas correctamente
- [ ] Sesión guardándose en localStorage o cookies
- [ ] Manejo de errores implementado
- [ ] Logout funcional

---

## 🚀 Próximos Pasos

1. **Seguridad:**
   - Implementar HTTPS
   - Usar HTTP-Only cookies
   - Agregar CSRF protection
   - Rate limiting

2. **Funcionalidades:**
   - Registro de usuarios
   - Recuperación de contraseña
   - Two-Factor Authentication
   - Social Login (Google, GitHub)

3. **UX:**
   - Loading states
   - Validaciones en tiempo real
   - Mensajes de error específicos
   - Remember me

---

## 📚 Recursos

- [Ejemplo HTML completo](./custom-login.html)
- [Componente React](./CustomLogin.jsx)
- [Spring Security Docs](https://docs.spring.io/spring-security/reference/)
- [OAuth 2.0 RFC](https://datatracker.ietf.org/doc/html/rfc6749)

---

## 💡 Preguntas Frecuentes

**Q: ¿Puedo usar este login con mi app móvil?**  
A: Sí, solo haz POST a `/api/auth/login` desde tu app.

**Q: ¿Cómo protejo mis endpoints?**  
A: Agrega el header `Authorization: Bearer {token}` en tus requests.

**Q: ¿Funciona con React Native / Flutter / Ionic?**  
A: Sí, cualquier framework que pueda hacer HTTP requests.

**Q: ¿Necesito usar el formulario de Spring Security?**  
A: No, puedes usar completamente tu propio diseño.
