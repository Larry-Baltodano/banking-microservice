package com.labg.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
		System.out.println("--> Notification Service iniciado en http://localhost:8084");
		System.out.println("	- Escuchando tópicos de kafka");
		System.out.println("	- Enviando notificaciones por email/SMS");
	}

}
