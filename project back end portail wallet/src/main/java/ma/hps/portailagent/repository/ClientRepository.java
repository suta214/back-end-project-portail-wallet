package ma.hps.portailagent.repository;

import ma.hps.portailagent.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Optional<Client> findById(Long id);
    Optional<Client> findByCin(String cin);
    Optional<Client> findByPhone(String phone);
    Optional<Client> findByEmail(String email);
    List<Client> findAll();
    List<Client> findByStatus(String status);
    List<Client> search(String searchTerm);
    Client save(Client client);
    Client update(Client client);
    void delete(Long id);
}
