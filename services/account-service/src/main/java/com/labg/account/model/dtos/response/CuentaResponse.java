package com.labg.account.model.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CuentaResponse {
    private Long id;
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal saldo;
    private String moneda;
    private String estado;
    private Long clienteId;
    private String clienteNombre;
    private LocalDateTime fechaApertura;
}
