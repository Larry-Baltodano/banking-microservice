package com.labg.gateway.controller;

import com.labg.gateway.request.AuthRequest;
import com.labg.gateway.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para login y validación de tokens")
public class AuthController {
    @Autowired
    private JwtUtil jwtUtil;

    private static final Map<String, UserInfo> users = Map.of(
            "admin", new UserInfo("admin", "admin123", List.of("ADMIN", "USER")),
            "user", new UserInfo("user", "user123", List.of("USER")),
            "larry", new UserInfo("larry", "password", List.of("USER"))
    );

    @PostMapping("/login")
    @Operation(summary = "Login de usuario", description = "Autentica al usuario y retorna un token JWT")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest request) {
        System.out.println("-- Login request para: " + request.getUsername());

        UserInfo user = users.get(request.getUsername());

        if (user == null || !user.password.equals(request.getPassword())) {
            System.out.println("-X Credenciales inválidas para: " + request.getUsername());
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        String token = jwtUtil.generateToken(request.getUsername(), user.roles);
        System.out.println("-> Token generado para: " + request.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", request.getUsername());
        response.put("roles", user.roles);
        response.put("expiresIn", 3600000);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validar token", description = "Varifica si un token JWT es válido")
    public ResponseEntity<Map<String, Object>> validate(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("valid", false));
        }

        String token = authHeader.substring(7);
        boolean isValid = jwtUtil.validateToken(token);

        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    record UserInfo(String username, String password, List<String> roles){}
}
