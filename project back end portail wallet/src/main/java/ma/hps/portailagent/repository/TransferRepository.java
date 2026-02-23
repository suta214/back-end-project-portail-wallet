package ma.hps.portailagent.repository;

import ma.hps.portailagent.model.Transfer;

import java.util.List;
import java.util.Optional;

public interface TransferRepository {
    Optional<Transfer> findById(Long id);
    Optional<Transfer> findByTransferId(String transferId);
    List<Transfer> findByAgentId(Long agentId);
    List<Transfer> findByStatus(String status);
    Transfer save(Transfer transfer);
    Transfer update(Transfer transfer);
    void delete(Long id);
}
