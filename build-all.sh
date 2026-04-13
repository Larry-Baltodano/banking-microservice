#!/bin/bash

echo "→ Construyendo todos los servicios..."

cd services/account-service
./mvnw clean package -DskipTests

cd ../transaction-service
./mvnw clean package -DskipTests

cd ../notification-service
./mvnw clean package -DskipTests

cd ../api-gateway
./mvnw clean package -DskipTests

cd ../..

echo "[✓] Construcción completada"
echo "[▲] Ejecutando docker-compose..."

docker-compose up -d --build

echo "[✓] Todos los servicios están en ejecución"
echo "[>] API Gateway: http://localhost:8080"
echo "[>] Swagger UI: http://localhost:8080/swagger-ui.html"
