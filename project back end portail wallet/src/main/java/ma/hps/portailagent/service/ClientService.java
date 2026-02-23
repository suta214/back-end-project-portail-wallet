package ma.hps.portailagent.service;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.ClientEnrollRequest;
import ma.hps.portailagent.dto.response.ClientResponse;
import ma.hps.portailagent.exception.ResourceNotFoundException;
import ma.hps.portailagent.model.Client;
import ma.hps.portailagent.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public List<ClientResponse> getClients(String status, String search) {
        List<Client> clients;
        
        if (search != null && !search.isEmpty()) {
            clients = clientRepository.search(search);
        } else if (status != null && !status.isEmpty()) {
            clients = clientRepository.findByStatus(status);
        } else {
            clients = clientRepository.findAll();
        }
        
        return clients.stream().map(this::toClientResponse).collect(Collectors.toList());
    }

    public ClientResponse getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        return toClientResponse(client);
    }

    public ClientResponse getClientByPhone(String phone) {
        Client client = clientRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        return toClientResponse(client);
    }

    public ClientResponse createClient(ClientEnrollRequest request) {
        if (clientRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Client with this phone already exists");
        }

        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setCin(request.getCin());
        client.setPhone(request.getPhone());
        client.setEmail(request.getEmail());
        client.setWalletId("WALLET-" + UUID.randomUUID().toString().substring(0, 12));
        client.setStatus("Actif");
        client.setKycVerified(false);
        client.setCreatedAt(LocalDateTime.now());

        Client saved = clientRepository.save(client);
        return toClientResponse(saved);
    }

    public ClientResponse updateClient(Long id, ClientEnrollRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        
        Client updated = clientRepository.save(client);
        return toClientResponse(updated);
    }

    private ClientResponse toClientResponse(Client client) {
        ClientResponse response = new ClientResponse();
        response.setId(client.getId());
        response.setFirstName(client.getFirstName());
        response.setLastName(client.getLastName());
        response.setCin(client.getCin());
        response.setPhone(client.getPhone());
        response.setEmail(client.getEmail());
        response.setWalletId(client.getWalletId());
        response.setBalance(BigDecimal.valueOf(0.00));
        response.setStatus(client.getStatus());
        response.setKycVerified(client.isKycVerified());
        return response;
    }
}
