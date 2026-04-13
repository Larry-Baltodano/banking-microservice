package com.labg.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
		System.out.println("--> API Gateway iniciado en http://localhost:8080");
		System.out.println("	- Account Service: http://localhost:8080/api/clientes");
		System.out.println("	- Transaction Service: http://localhost:8080/api/transacciones");
	}

}
