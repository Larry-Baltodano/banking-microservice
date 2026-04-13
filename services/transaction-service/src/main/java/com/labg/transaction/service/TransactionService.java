package com.labg.transaction.service;

import com.labg.transaction.model.dto.request.TransferenciaRequest;
import com.labg.transaction.model.dto.response.TransferenciaResponse;
import com.labg.transaction.model.entity.Transaccion;

public interface TransactionService {
    TransferenciaResponse procesarTransferencia(TransferenciaRequest request, String idempotencyKey);
    TransferenciaResponse obtenerEstadoTransaccion(Long transaccionId);
    TransferenciaResponse obtenerEstadoTransaccionPorIdempotencyKey(String idempotencyKey);
    void revertirTransaccion(Long transaccionId, String motivo);
    Transaccion obtenerTransaccionEntity(Long id);
}
