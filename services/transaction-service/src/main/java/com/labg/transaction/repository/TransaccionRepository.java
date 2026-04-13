package com.labg.transaction.repository;

import com.labg.transaction.model.entity.Transaccion;
import com.labg.transaction.model.enums.EstadoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    Optional<Transaccion> findByIdempotencyKey(String idempotencyKey);

    List<Transaccion> findByCuentaOrigenIdOrCuentaDestinoId(Long cuentaOrigenId, Long cuentaDestinoId);
    List<Transaccion> findByEstadoAndFechaCreacionBefore(EstadoTransaccion estado, LocalDateTime fecha);
    boolean existsByIdempotencyKey(String idempotencyKey);
    long countByCuentaOrigenIdAndFechaCreacionBetween(Long cuentaOrigenId, LocalDateTime inicio, LocalDateTime fin);
}
