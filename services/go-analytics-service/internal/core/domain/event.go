package domain

type TransactionEvent struct {
	TransaccionID int64 `json:"transaccionId"`
	CuentaOrigenID int64 `json:"cuentaOrigenId"`
	CuentaDestinoID int64 `json:"cuentaDestinoId"`
	Monto float64 `json:"monto"`
	Tipo string `json:"tipo"`
	Estado string `json:"estado"`
	Descripcion string `json:"descripcion"`
	Fecha string `json:"fecha"`
	ErrorMensaje string `json:"errorMensaje"`
}