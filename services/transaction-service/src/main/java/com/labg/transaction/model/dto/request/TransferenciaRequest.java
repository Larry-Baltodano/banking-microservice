package com.labg.transaction.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaRequest {
    @NotNull(message = "La cuenta origen es obligatoria")
    @Positive(message = "El ID de cuenta debe ser positivo")
    private Long cuentaOrigenId;

    @NotNull(message = "La cuenta destino es obligatoria")
    @Positive(message = "El ID de cuenta debe ser positivo")
    private Long cuentaDestinoId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto minimo es de 0.01")
    private BigDecimal monto;

    private String descripcion;
    private String referencia;
}
