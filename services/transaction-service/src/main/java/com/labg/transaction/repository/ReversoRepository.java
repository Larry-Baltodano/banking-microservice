package com.labg.transaction.repository;

import com.labg.transaction.model.entity.Reverso;
import com.labg.transaction.model.enums.EstadoReverso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReversoRepository extends JpaRepository<Reverso, Long> {
    Optional<Reverso> findByTransaccionOriginalId(Long transaccionId);
    List<Reverso> findByEstado(EstadoReverso estado);
    boolean existsByTransaccionOriginalId(Long transaccionId);
}
