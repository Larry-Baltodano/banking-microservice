package com.labg.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
public class TransactionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionServiceApplication.class, args);
		System.out.println("--> Transaction Service iniciado en http://localhost:8082");
		System.out.println("	- Transferencias: POST /api/transacciones/transferencias");
		System.out.println("	- Estado: GET /api/transacciones/{id}");
	}

}
