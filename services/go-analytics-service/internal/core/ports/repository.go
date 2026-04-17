package ports

import "github.com/Larry-Baltodano/go-analytics-service/internal/core/domain"


type MetricRepository interface {
	UpdateMetrics(event domain.TransactionEvent) error
	GetCurrentMetrics() (*domain.Metric, error)
	SaveFraudAlert(alert domain.FraudAlert) error
	GetPendingAlerts() ([]domain.FraudAlert, error)
}