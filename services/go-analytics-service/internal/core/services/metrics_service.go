package services

import (
	"log"
	"strconv"
	"sync"
	"time"

	"github.com/Larry-Baltodano/go-analytics-service/internal/core/domain"
	"github.com/Larry-Baltodano/go-analytics-service/internal/core/ports"
	"github.com/google/uuid"
)

type MetricsService struct {
	repo ports.MetricRepository
	consumer ports.KafkaConsumer
	notifier ports.Notifier
	mu sync.RWMutex
	metrics *domain.Metric
}

func NewMetricsService(repo ports.MetricRepository, consumer ports.KafkaConsumer, notifier ports.Notifier) *MetricsService {
	return &MetricsService{
		repo: repo,
		consumer: consumer,
		notifier: notifier,
		metrics: &domain.Metric{
			TotalTransferido: 0,
			CantidadTransacciones: 0,
			PromedioMonto: 0,
			MaxTransferencia: 0,
			MinTransferencia: 1 << 62,
			HoraActualizacion: time.Now().Format(time.RFC3339),
		},
	}
}

func (s *MetricsService) Start() error {
	log.Println("Starting MetricsService...")

	events, errs := s.consumer.ConsumeEvents()

	log.Println("Waiting for kafka evets...")

	go func() {
		for event := range events {
			log.Printf(">> Processing event: TransaccionId=%d, Monto=%f", event.CuentaDestinoID, event.Monto)
			s.processEvent(event)
		}
	}()

	go func ()  {
		for err := range errs {
			log.Printf("X - Error consuming event: %v", err)
		}
	}()

	return nil
}

func (s *MetricsService) processEvent(event domain.TransactionEvent) {
	if event.Estado != "COMPLETADA" {
		return
	}

	s.mu.Lock()
	defer s.mu.Unlock()

	s.metrics.TotalTransferido += event.Monto
	s.metrics.CantidadTransacciones++
	s.metrics.PromedioMonto = s.metrics.TotalTransferido / float64(s.metrics.CantidadTransacciones)
	s.metrics.HoraActualizacion = time.Now().Format(time.RFC3339)

	if event.Monto > s.metrics.MaxTransferencia {
		s.metrics.MaxTransferencia = event.Monto
	}
	if event.Monto < s.metrics.MinTransferencia {
		s.metrics.MinTransferencia = event.Monto
	}

	if err := s.repo.UpdateMetrics(event); err != nil {
		log.Printf("Error updating metrics: %v", err)
	}

	if event.Monto > 10000 {
		alert := domain.FraudAlert{
			ID: string(uuid.New().String()),
			CuentaID: event.CuentaOrigenID,
			TipoAlerta: "MONTO_ALTO",
			Monto: event.Monto,
			TransaccionID: event.TransaccionID,
			Fecha: time.Now().Format(time.RFC3339),
			Estado: "PENDIENTE",
		}
		if err := s.repo.SaveFraudAlert(alert); err != nil {
			log.Printf("Error saving fraud alerte: %v", err)
		}

		if s.notifier != nil {
			s.notifier.SendWebSocketAlert("△ Alerta: Transferencia de alto monto detectada - $" +
			 formatFloat(event.Monto) + " desde cuenta " + formatInt(event.CuentaOrigenID))
		}
	}
}

func (s *MetricsService) GetCurrentMetrics() *domain.Metric {
	s.mu.RLock()
	defer s.mu.RUnlock()
	return s.metrics
}

func formatFloat(f float64) string {	
	return strconv.FormatFloat(f, 'f', 2, 64)
}

func formatInt(i int64) string {
	return strconv.FormatInt(i, 10)
}