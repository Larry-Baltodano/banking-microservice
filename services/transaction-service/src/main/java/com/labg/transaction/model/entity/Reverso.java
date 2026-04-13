package com.labg.transaction.model.entity;

import com.labg.transaction.model.enums.EstadoReverso;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reversos")
public class Reverso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaccion_original_id", nullable = false)
    private Long transaccionOriginalId;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReverso estado;

    @Column(name = "fecha_solicitud")
    @CreationTimestamp
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_completado")
    private LocalDateTime fechaCompletado;

    @Column(name = "error_mensaje")
    private String errorMensaje;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}
