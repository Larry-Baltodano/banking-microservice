package com.labg.account.service.impl;

import com.labg.account.exception.ClienteNoEncontradoException;
import com.labg.account.exception.DuplicateEmailException;
import com.labg.account.model.dtos.request.CrearClienteRequest;
import com.labg.account.model.dtos.response.ClienteResponse;
import com.labg.account.model.entity.Cliente;
import com.labg.account.model.enums.EstadoCliente;
import com.labg.account.repository.ClienteRepository;
import com.labg.account.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public ClienteResponse crearCliente(CrearClienteRequest request) {
        log.info("Creando nuevo cliente con email: {}", request.getEmail());

        if (clienteRepository.existsByEmail(request.getEmail())) {
            log.warn("Intento de crear cliente con email duplicado: {}", request.getEmail());
            throw new DuplicateEmailException(request.getEmail());
        }

        Cliente cliente = Cliente.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .documento(request.getDocumento())
                .estado(EstadoCliente.ACTIVO)
                .build();

        cliente = clienteRepository.save(cliente);
        log.info("Cliente creado exitosamente con ID: {}", cliente.getId());

        return convertToDTO(cliente);
    }

    @Override
    @Cacheable(value = "clientes", key = "#id")
    public ClienteResponse obtenerClientePorId(Long id) {
        log.debug("Buscando cliente por ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));

        return convertToDTO(cliente);
    }

    @Override
    public ClienteResponse obtenerClientePorEmail(String email) {
        log.debug("Buscando cliente por email: {}", email);

        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNoEncontradoException(email));

        return convertToDTO(cliente);
    }

    @Override
    public List<ClienteResponse> listarTodosClientes() {
        log.debug("Listando todos los clientes");

        return clienteRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "clientes", key = "#id")
    public ClienteResponse actualizarCliente(Long id, CrearClienteRequest request) {
        log.info("Actualizando cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));

        if (!cliente.getEmail().equals(request.getEmail()) &&
        clienteRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        cliente.setNombre(request.getNombre());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setDocumento(request.getDocumento());

        cliente = clienteRepository.save(cliente);
        log.info("Cliente actualizado exitosamente ID: {}", id);

        return convertToDTO(cliente);
    }

    @Override
    @Transactional
    @CacheEvict(value = "clientes", key = "#id")
    public void eliminarCliente(Long id) {
        log.info("Eliminando cliente ID: {}", id);

        if (!clienteRepository.existsById(id)) {
            throw new ClienteNoEncontradoException(id);
        }

        Cliente cliente = clienteRepository.findById(id).get();
        cliente.setEstado(EstadoCliente.INACTIVO);
        clienteRepository.save(cliente);

        log.info("Cliente marcado como INACTIVO ID: {}", id);
    }

    @Override
    public boolean existeClientePorEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    @Override
    public Cliente obtenerClienteEntity(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));
    }

    private ClienteResponse convertToDTO(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .documento(cliente.getDocumento())
                .estado(cliente.getEstado().name())
                .fechaRegistro(cliente.getFechaRegistro())
                .build();
    }
}
