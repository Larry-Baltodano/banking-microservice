package postgres

import (
	"database/sql"

	"github.com/Larry-Baltodano/go-analytics-service/internal/core/domain"
	"github.com/Larry-Baltodano/go-analytics-service/internal/core/ports"
)

type MetricRepository struct {
	db *sql.DB
}

func NewMetricRepository(db *sql.DB) ports.MetricRepository {
	return &MetricRepository{db: db}
}

func (r *MetricRepository) UpdateMetrics(event domain.TransactionEvent) error {
	query := `INSERT INTO metrics (total_transferido, cantidad_transacciones, promedio_monto, max_transferencia, min_transferencia, hora_actualizacion)
              VALUES ($1, $2, $3, $4, $5, $6)
              ON CONFLICT (id) DO UPDATE SET
                total_transferido = EXCLUDED.total_transferido,
                cantidad_transacciones = EXCLUDED.cantidad_transacciones,
                promedio_monto = EXCLUDED.promedio_monto,
                max_transferencia = EXCLUDED.max_transferencia,
                min_transferencia = EXCLUDED.min_transferencia,
                hora_actualizacion = EXCLUDED.hora_actualizacion`
	
	_, err := r.db.Exec(query, event.Monto, 1, event.Monto, event.Monto, event.Monto, event.Fecha)
	return err
}

func (r *MetricRepository) GetCurrentMetrics() (*domain.Metric, error) {
	return &domain.Metric{}, nil
}

func (r *MetricRepository) SaveFraudAlert(alert domain.FraudAlert) error {
	query := `INSERT INTO fraud_alerts (id, cuenta_id, tipo_alerta, monto, transaccion_id, fecha, estado)
			  VALUES ($1, $2, $3, $4, $5, $6, $7)`
	_, err := r.db.Exec(query, alert.ID, alert.CuentaID, alert.TipoAlerta, alert.Monto, alert.TransaccionID, alert.Fecha, alert.Estado)
	return err
}

func (r *MetricRepository) GetPendingAlerts() ([]domain.FraudAlert, error) {
	return []domain.FraudAlert{}, nil
}