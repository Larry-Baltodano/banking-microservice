package ports

import "github.com/Larry-Baltodano/go-analytics-service/internal/core/domain"

type KafkaConsumer interface {
	ConsumeEvents() (<-chan domain.TransactionEvent, <-chan error)
	Close() error
}