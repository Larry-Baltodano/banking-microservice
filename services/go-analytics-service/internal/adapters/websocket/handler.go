package websocket

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
)

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

type WebSocketHandler struct {
	notifier *Notifier
}

func NewWebSocketHandler(notifier *Notifier) *WebSocketHandler {
	return &WebSocketHandler{
		notifier: notifier,
	}
}

func (h *WebSocketHandler) HandlerWebSocket(c *gin.Context) {
	conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to upgrade connection"})
		return
	}

	h.notifier.AddClient(conn)
	defer h.notifier.RemoveClient(conn)

	for {
		_, _, err := conn.ReadMessage()
		if err != nil {
			break
		}
	}
}