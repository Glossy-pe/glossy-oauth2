package com.example.oauth.controller;

import com.example.oauth.dto.LoginRequest;
import com.example.oauth.dto.LoginResponse;
import com.example.oauth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API REST para login personalizado
 * Permite que tu frontend envíe credenciales directamente sin usar el form de Spring Security
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Ajusta esto en producción
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final RegisteredClientRepository registeredClientRepository;

    /**
     * Endpoint de login personalizado
     * POST /api/auth/login
     * Body: { "username": "user", "password": "password" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // Extraer roles
            String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

            // Crear respuesta
            LoginResponse response = LoginResponse.builder()
                .username(authentication.getName())
                .roles(roles)
                .message("Login exitoso")
                .authenticated(true)
                .build();

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales inválidas");
            error.put("message", "Usuario o contraseña incorrectos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Endpoint para obtener token OAuth usando credenciales
     * POST /api/auth/token
     * Body: { "username": "user", "password": "password", "client_id": "public-client", "client_secret": "secret" }
     */
    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody LoginRequest loginRequest) {
        try {
            // Autenticar usuario primero
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // Aquí deberías generar el token OAuth2 real
            // Por ahora retornamos información básica
            TokenResponse response = TokenResponse.builder()
                .username(authentication.getName())
                .authenticated(true)
                .message("Para obtener un access_token real, usa el endpoint /oauth2/token con client credentials")
                .build();

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Endpoint para verificar si el usuario está autenticado
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "No autenticado"));
        }

        Map<String, Object> user = new HashMap<>();
        user.put("username", authentication.getName());
        user.put("authorities", authentication.getAuthorities());
        user.put("authenticated", true);

        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint de logout
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of(
            "message", "Sesión cerrada exitosamente",
            "success", true
        ));
    }
}
