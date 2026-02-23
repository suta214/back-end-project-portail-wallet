package ma.hps.portailagent.repository;

import ma.hps.portailagent.model.AuditLog;

import java.util.List;
import java.util.Optional;

public interface AuditRepository {
    Optional<AuditLog> findById(Long id);
    List<AuditLog> findByAgentId(Long agentId);
    List<AuditLog> findAll();
    AuditLog save(AuditLog auditLog);
    void delete(Long id);
}
