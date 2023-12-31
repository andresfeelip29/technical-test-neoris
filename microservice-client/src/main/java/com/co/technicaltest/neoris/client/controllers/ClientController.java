package com.co.technicaltest.neoris.client.controllers;

import com.co.technicaltest.neoris.client.models.dto.ClientDTO;
import com.co.technicaltest.neoris.client.models.dto.ClientQueryDTO;
import com.co.technicaltest.neoris.client.models.dto.ClientResponseDTO;
import com.co.technicaltest.neoris.client.services.ClientService;
import domain.models.ClientAccountQueryDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1/clientes")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/")
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        log.info("Se recibe peticion para consulta de todos los clientes");
        return ResponseEntity.ok(this.clientService.getAllClient());
    }

    @GetMapping("/detail/{clientId}")
    public ResponseEntity<ClientResponseDTO> findClientByIdWithAccountDetail(@PathVariable Long clientId) {
        log.info("Se recibe peticion de consulta de cliente con detalle con id: {}", clientId);
        return this.clientService.findClientAccountDetail(clientId)
                .map(client -> ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(client))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponseDTO> findClientById(@PathVariable Long clientId) {
        log.info("Se recibe peticion de consulta de cliente con id: {}", clientId);
        return this.clientService.findClientById(clientId)
                .map(client -> ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(client))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/external/{clientId}")
    public ResponseEntity<ClientQueryDTO> findClientByIdFromMicroserviceAccount(@PathVariable Long clientId) {
        log.info("Se recibe peticion de consulta desde microservicios de cuentas, de cliente con id: {}", clientId);
        return this.clientService.findClientByIdFromMicroserviceAccount(clientId)
                .map(client -> ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(client))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/")
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientDTO clientDTO) {
        log.info("Se recibe peticion para almacenar cliente {}!", clientDTO);
        ClientResponseDTO result = this.clientService.saveClient(clientDTO);
        if (!Objects.isNull(result)) {
            log.info("Se crea cliente con id: {}", result.id());
            return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(result);
        }
        log.error("Error en solicitud de creacion de cliente {}!", clientDTO);
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/external/")
    public ResponseEntity<Void> saveClientAccountFromMicroserviceClient(@Valid @RequestBody ClientAccountQueryDTO clientAccountQueryDTO) {
        log.info("Se recibe peticion desde microservicio cuentas, para asociar cuenta: {} , a usuario con id: {}",
                clientAccountQueryDTO.accountId(), clientAccountQueryDTO.clientId());
        this.clientService.saveClientAccountFromMicroserviceClient(clientAccountQueryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @Valid @RequestBody ClientDTO clientDTO, @PathVariable Long clientId) {
        ClientResponseDTO result = this.clientService.updateClient(clientDTO, clientId);
        if (!Objects.isNull(result)) {
            log.info("Cliente actualizado con exito!");
            return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(result);
        }
        log.error("Error en solicitud de actualizacion de cliente!");
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long clientId) {
        Boolean result = this.clientService.deleteClient(clientId);
        if (!Objects.isNull(result)) {
            log.info("Cliente eliminado con exito!");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.APPLICATION_JSON).build();
        }
        log.error("Error en solicitud de eliminacion de cliente!");
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/external/")
    public ResponseEntity<Void> deleteAccountClientFromMicroserviceAccount(@RequestParam Long clientId,  @RequestParam Long accountId) {
        log.info("Se recibe peticion desde microservicio cuentas, para la eliminacion de cuenta con id: {}", accountId);
        this.clientService.deleteAccountClientFromMicroserviceAccount(clientId, accountId);
        return ResponseEntity.noContent().build();
    }

}
