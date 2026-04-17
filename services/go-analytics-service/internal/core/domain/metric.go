package domain

type Metric struct {
	TotalTransferido float64 `json:"totalTransferido"`
	CantidadTransacciones int64 `json:"cantidadTransacciones"`
	PromedioMonto float64 `json:"promedioMonto"`
	MaxTransferencia float64 `json:"maxTransferencia"`
	MinTransferencia float64 `json:"minTransferencia"`
	HoraActualizacion string `json:"horaActualizacion"`
}

type FraudAlert struct {
	ID string `json:"id"`
	CuentaID int64 `json:"cuentaId"`
	TipoAlerta string `json:"tipoAlerta"`
	Monto float64 `json:"monto"`
	TransaccionID int64 `json:"transaccionId"`
	Fecha string `json:"fecha"`
	Estado string `json:"estado"`
}