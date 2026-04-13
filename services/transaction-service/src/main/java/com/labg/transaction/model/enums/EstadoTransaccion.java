package com.labg.transaction.model.enums;

public enum EstadoTransaccion {
    PENDIENTE("Pendiente de procesamiento"),
    VALIDANDO_FONDOS("Validando fondos en cuenta origen"),
    DEBITANDO_ORIGEN("Debitando cuenta origen"),
    ACREDITANDO_DESTINO("Acreditando cuenta destino"),
    COMPLETADA("Completada exitosamente"),
    FALLIDA("Fallida durante el proceso"),
    REVERTIDA("Revertida por error"),
    EN_REVERSO("En proceso de reverso");

    private final String descripcion;

    EstadoTransaccion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
