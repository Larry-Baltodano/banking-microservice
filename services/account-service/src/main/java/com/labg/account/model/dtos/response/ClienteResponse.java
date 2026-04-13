package com.labg.account.model.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponse {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String documento;
    private String estado;
    private java.time.LocalDateTime fechaRegistro;
}
