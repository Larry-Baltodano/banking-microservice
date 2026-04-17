package ports

type Notifier interface {
	SendWebSocketAlert(message string) error
}