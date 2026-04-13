package com.labg.gateway.filter;

import com.labg.gateway.utils.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/auth",
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator",
            "/error",
            "/favicon.ico"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println("🔐 Ruta protegida (Filtro ejecutándose): " + path);

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Token no proporcionado para la ruta: " + path);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token no proporcionado");
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("🔑 Token recibido: " + token.substring(0, Math.min(token.length(), 20)) + "...");

        if (!jwtUtil.validateToken(token)) {
            System.out.println("❌ Token inválido o expirado");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token inválido o expirado");
            return;
        }

        String username = jwtUtil.extractUsername(token);
        List<String> roles = jwtUtil.extractRoles(token);
        System.out.println("✅ Usuario autenticado: " + username + ", Roles: " + roles);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        request.setAttribute("X-Auth-User", username);
        request.setAttribute("X-Auth-Roles", String.join(",", roles));

        filterChain.doFilter(request, response);
    }
}
