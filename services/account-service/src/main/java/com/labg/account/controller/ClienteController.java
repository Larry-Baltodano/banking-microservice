package com.labg.account.controller;

import com.labg.account.model.dtos.request.CrearClienteRequest;
import com.labg.account.model.dtos.response.ClienteResponse;
import com.labg.account.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.SeparatorUI;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Endpoints para gestión de clientes")
public class ClienteController {
    private final ClienteService clienteService;

    @PostMapping
    @Operation(summary = "Crear nuevo cliente", description = "Registra un nuevo cliente en el sistema")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente",
            content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    public ResponseEntity<Map<String, Object>> crearCliente(@Valid @RequestBody CrearClienteRequest request) {
        ClienteResponse cliente = clienteService.crearCliente(request);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Cliente creado exitosamente");
        response.put("cliente", cliente);

        return ResponseEntity.created(URI.create("/api/clientes/" + cliente.getId())).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID", description = "Retorna los datos de un cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontado"),
            @ApiResponse(responseCode = "404", description = "Cliente no enontrado")
    })
    public ResponseEntity<ClienteResponse> obtenerCliente(@PathVariable Long id) {
        ClienteResponse cliente = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping
    @Operation(summary = "Listar todos los clientes", description = "Retorna la lista completa de clientes")
    @ApiResponse(responseCode = "200", description = "Lista de clientes")
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        List<ClienteResponse> clientes = clienteService.listarTodosClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar cliente por email", description = "Retorna un cliente según su email")
    public ResponseEntity<ClienteResponse> obtenerClientePorEmail(@PathVariable String email) {
        ClienteResponse cliente = clienteService.obtenerClientePorEmail(email);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente")
    public ResponseEntity<ClienteResponse> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody CrearClienteRequest request) {
        ClienteResponse cliente = clienteService.actualizarCliente(id, request);
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente", description = "Elimina (desactiva) un cliente del sistema")
    @ApiResponse(responseCode = "200", description = "Cliente eliminado")
    public ResponseEntity<Map<String, String>> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Cliente eliminado exitosamente");
        return ResponseEntity.ok(response);
    }
}
