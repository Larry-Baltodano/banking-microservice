package com.labg.transaction.model.dto.response;

import com.labg.transaction.model.enums.EstadoTransaccion;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransferenciaResponse {
    private Long transaccionId;
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;
    private EstadoTransaccion estado;
    private String mensaje;
    private LocalDateTime fecha;
    private String idempotencyKey;
}
