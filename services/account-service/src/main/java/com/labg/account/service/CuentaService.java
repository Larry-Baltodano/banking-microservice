package com.labg.account.service;

import com.labg.account.model.dtos.request.CrearCuentaRequest;
import com.labg.account.model.dtos.response.CuentaResponse;
import com.labg.account.model.dtos.response.ValidacionFondosResponse;
import com.labg.account.model.entity.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {
    CuentaResponse crearCuenta(CrearCuentaRequest request);
    CuentaResponse obtenerCuentaPorId(Long id);
    CuentaResponse obtenerCuentaPorNumero(String numeroCuenta);
    List<CuentaResponse> listarCuentasPorCliente(Long clienteId);
    CuentaResponse debitarCuenta(Long cuentaId, BigDecimal monto, String transaccionId);
    CuentaResponse acreditarCuenta(Long cuentaId, BigDecimal monto, String transaccionId);
    BigDecimal consultarSaldo(Long cuentaId);
    ValidacionFondosResponse validarFondos(Long cuentaId, BigDecimal monto);
    Cuenta obtenerCuentaEntity(Long id);
}
