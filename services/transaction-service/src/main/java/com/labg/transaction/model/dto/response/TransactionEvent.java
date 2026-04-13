package com.labg.transaction.model.dto.response;

import com.labg.transaction.model.enums.EstadoTransaccion;
import com.labg.transaction.model.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private Long transaccionId;
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;
    private TipoTransaccion tipo;
    private EstadoTransaccion estado;
    private String descripcion;
    private String fecha;
    private String errorMensaje;
}
