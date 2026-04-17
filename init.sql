CREATE DATABASE accountdb;
CREATE DATABASE transactiondb;
CREATE DATABASE analyticsdb;

\c analyticsdb;

CREATE TABLE IF NOT EXISTS metrics(
    id SERIAL PRIMARY KEY,
    total_transferido DECIMAL(15,2) DEFAULT 0,
    cantidad_transacciones BIGINT DEFAULT 0,
    promedio_monto DECIMAL(15,2) DEFAULT 0,
    max_transferencia DECIMAL(15,2) DEFAULT 0,
    min_transferencia DECIMAL(15,2) DEFAULT 0,
    hora_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO metrics (id, total_transferido, cantidad_transacciones, promedio_monto, max_transferencia, min_transferencia)
VALUES (1, 0, 0, 0, 0, 0) ON CONFLICT (id) DO NOTHING;

CREATE TABLE IF NOT EXISTS fraud_alerts (
    id VARCHAR(36) PRIMARY KEY,
    cuenta_id BIGINT NOT NULL,
    tipo_alerta VARCHAR(50) NOT NULL,
    monto DECIMAL(15,2) NOT NULL,
    transaccion_id BIGINT NOT NULL,
    fecha TIMESTAMP NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE'
);