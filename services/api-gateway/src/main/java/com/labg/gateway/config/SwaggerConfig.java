package com.labg.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Banking API Gateway")
                        .version("1.0")
                        .description("""
                                ## Sistema Bancario con Microservicios
                                
                                Este API Gateway centraliza el acceso a los siguientes servicios:
                                
                                - **Account Service**: Gestión de clientes y cuentas
                                - **Transaction Service**: Transferencias bancarias
                                - **Notification Service**: Notificaciones por email/SMS
                                
                                ### Autenticación
                                1. Obtén un token JWT en `/auth/login`
                                2. Usa el token en el header `Authorization: Bearer <token>`
                                
                                ### Credenciales de prueba
                                - `larry` / `password` (rol: USER)
                                - `admin` / `admin123` (rol: ADMIN)
                                """)
                        .contact(new Contact()
                                .name("Larry Baltodano")
                                .email("baltolarry.23@gmail.com")
                                .url("https://github.com/Larry-Baltodano"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa el token JWT obtenido en /auth/login")));
    }
}
