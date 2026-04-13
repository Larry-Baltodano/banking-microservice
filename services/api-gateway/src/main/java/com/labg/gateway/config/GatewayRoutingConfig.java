package com.labg.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayRoutingConfig {
    @Bean
    public RouterFunction<ServerResponse> accountServiceRoutes() {
        return route("account-service")
                .route(path("/api/clientes/**"), http())
                .before(uri("http://localhost:8081"))
                .build()
                .and(route("account-service-cuentas")
                        .route(path("/api/cuentas/**"), http())
                        .before(uri("http://localhost:8081"))
                        .build()
                );
    }

    @Bean
    public RouterFunction<ServerResponse> transactionRoutes() {
        return route("transaction-service")
                .route(path("/api/transacciones/**"), http())
                .before(uri("http://localhost:8082"))
                .build();
    }
}
