package ma.hps.portailagent.repository;

import ma.hps.portailagent.model.TransactionLog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Optional<TransactionLog> findById(Long id);
    Optional<TransactionLog> findByTransactionId(String transactionId);
    List<TransactionLog> findAll();
    List<TransactionLog> findByAgentId(Long agentId);
    List<TransactionLog> findByStatus(String status);
    List<TransactionLog> findByType(String type);
    List<TransactionLog> search(String searchTerm);
    List<TransactionLog> findByAgentIdPaginated(Long agentId, int page, int pageSize);
    long countByAgentId(Long agentId);
    TransactionLog save(TransactionLog transaction);
    TransactionLog update(TransactionLog transaction);
    void delete(Long id);

    // Stats dashboard
    long countTodayByAgentIdAndType(Long agentId, String type);
    BigDecimal sumTodayAmountByAgentIdAndType(Long agentId, String type);
    long countAllToday();
    BigDecimal sumAllAmountToday();
    long countPending();
}
