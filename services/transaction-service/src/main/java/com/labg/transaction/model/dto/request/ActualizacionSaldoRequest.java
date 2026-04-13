package com.labg.transaction.model.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ActualizacionSaldoRequest {
    private BigDecimal monto;
    private String transaccionId;
    private boolean esReverso;
}
