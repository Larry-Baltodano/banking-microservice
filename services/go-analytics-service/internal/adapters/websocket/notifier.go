package websocket

import (
	"log"
	"sync"

	"github.com/gorilla/websocket"
)

type Notifier struct {
	clients map[*websocket.Conn]bool
	broadcast chan string
	mu sync.Mutex
}

func NewNotifier() *Notifier {
	return &Notifier{
		clients: make(map[*websocket.Conn]bool),
		broadcast: make(chan string),
	}
}

func (n *Notifier) SendWebSocketAlert(message string) error {
	n.broadcast <- message
	return nil
}

func (n *Notifier) Run() {
	for {
		msg := <-n.broadcast
		n.mu.Lock()
		for client := range n.clients {
			err := client.WriteMessage(websocket.TextMessage, []byte(msg))
			if err != nil {
				log.Printf("WebSocket error: %v", err)
				client.Close()
				delete(n.clients, client)
			}
		}
		n.mu.Unlock()
	}
}

func (n *Notifier) AddClient(conn *websocket.Conn) {
	n.mu.Lock()
	defer n.mu.Unlock()
	n.clients[conn] = true
}

func (n *Notifier) RemoveClient(conn *websocket.Conn) {
	n.mu.Lock()
	defer n.mu.Unlock()
	delete(n.clients, conn)
}