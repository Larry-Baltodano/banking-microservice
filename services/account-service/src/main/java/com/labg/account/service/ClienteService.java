package com.labg.account.service;

import com.labg.account.model.dtos.request.CrearClienteRequest;
import com.labg.account.model.dtos.response.ClienteResponse;
import com.labg.account.model.entity.Cliente;

import java.util.List;

public interface ClienteService {
    ClienteResponse crearCliente(CrearClienteRequest request);
    ClienteResponse obtenerClientePorId(Long id);
    ClienteResponse obtenerClientePorEmail(String email);
    List<ClienteResponse> listarTodosClientes();
    ClienteResponse actualizarCliente(Long id, CrearClienteRequest request);
    void eliminarCliente(Long id);
    boolean existeClientePorEmail(String email);
    Cliente obtenerClienteEntity(Long id);
}
