package main

import (
	"database/sql"
	"log"

	_ "github.com/lib/pq"

	"github.com/Larry-Baltodano/go-analytics-service/internal/adapters/handlers/http"
	"github.com/Larry-Baltodano/go-analytics-service/internal/adapters/kafka"
	"github.com/Larry-Baltodano/go-analytics-service/internal/adapters/repositories/postgres"
	"github.com/Larry-Baltodano/go-analytics-service/internal/adapters/websocket"
	"github.com/Larry-Baltodano/go-analytics-service/internal/core/services"
	"github.com/gin-gonic/gin"
)

func main() {
	connStr := "postgresql://admin:secret123@localhost:5433/analyticsdb?sslmode=disable"
	db, err := sql.Open("postgres", connStr)
	if err != nil {
		log.Fatal("Error connecting to database:", err)
	}
	defer db.Close()

	if err := db.Ping(); err != nil {
		log.Fatal("Error pinging database:", err)
	}
	log.Println("✓ Connected to PostgreSQL")

	repo := postgres.NewMetricRepository(db)

	wsNotifier := websocket.NewNotifier()
	go wsNotifier.Run()

	consumer, err := kafka.NewConsumer([]string{"localhost:9092"}, "transacciones-completadas")
	if err != nil {
		log.Fatal("Error creating Kafka consumer:", err)
	}
	defer consumer.Close()

	metricsService := services.NewMetricsService(repo, consumer, wsNotifier)
	if err := metricsService.Start(); err != nil {
		log.Fatal("Error starting metrics service:", err)
	}

	handler := http.NewHandler(metricsService, wsNotifier)

	router := gin.Default()
	handler.SetupRoutes(router)

	log.Println("→ Analytics Service running on port 8085")
	log.Println("	- Metrics: GET /api/analytics/metrics")
	log.Println("	- WebSocket: ws://localhost:8085/ws")

	router.Run(":8085")
}