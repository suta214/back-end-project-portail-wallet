package ma.hps.portailagent.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.ClientEnrollRequest;
import ma.hps.portailagent.dto.response.ClientResponse;
import ma.hps.portailagent.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
@PreAuthorize("hasAnyRole('CLIENT_MGMT')")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getClients(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        log.info("Fetching clients with status: {}, search: {}", status, search);
        List<ClientResponse> clients = clientService.getClients(status, search);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClient(@PathVariable Long id) {
        log.info("Fetching client with id: {}", id);
        ClientResponse client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/search")
    public ResponseEntity<ClientResponse> searchByPhone(@RequestParam String phone) {
        log.info("Searching client by phone: {}", phone);
        ClientResponse client = clientService.getClientByPhone(phone);
        return ResponseEntity.ok(client);
    }

    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientEnrollRequest request) {
        log.info("Creating new client: {}", request.getFirstName());
        ClientResponse client = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable Long id, @Valid @RequestBody ClientEnrollRequest request) {
        log.info("Updating client with id: {}", id);
        ClientResponse client = clientService.updateClient(id, request);
        return ResponseEntity.ok(client);
    }
}
