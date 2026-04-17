package http

import (
	"net/http"

	"github.com/Larry-Baltodano/go-analytics-service/internal/adapters/websocket"
	"github.com/Larry-Baltodano/go-analytics-service/internal/core/services"
	"github.com/gin-gonic/gin"
)

type Handler struct {
	metricsService *services.MetricsService
	wsHandler *websocket.WebSocketHandler
}

func NewHandler(metricsService *services.MetricsService, wsNotifier *websocket.Notifier) *Handler {
	return &Handler{
		metricsService: metricsService,
		wsHandler: websocket.NewWebSocketHandler(wsNotifier),
	}
}

func (h *Handler) GetMetrics(c *gin.Context) {
	metrics := h.metricsService.GetCurrentMetrics()
	c.JSON(http.StatusOK, metrics)
}

func (h *Handler) SetupRoutes(router *gin.Engine) {
	api := router.Group("/api/analytics") 
	{
		api.GET("/metrics", h.GetMetrics)
	}

	router.GET("/ws", h.wsHandler.HandlerWebSocket)
}