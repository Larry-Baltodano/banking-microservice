package com.labg.account.model.dtos.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActualizacionSaldoRequest {
    private BigDecimal monto;
    private String transaccionId;
    private boolean esReverso;
}
