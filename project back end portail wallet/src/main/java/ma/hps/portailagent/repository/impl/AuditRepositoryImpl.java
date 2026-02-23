package ma.hps.portailagent.repository.impl;

import ma.hps.portailagent.model.AuditLog;
import ma.hps.portailagent.repository.AuditRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AuditRepositoryImpl implements AuditRepository {
    private final JdbcTemplate jdbcTemplate;

    public AuditRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AuditLog> auditLogRowMapper = (rs, rowNum) -> {
        AuditLog auditLog = new AuditLog();
        auditLog.setId(rs.getLong("id"));
        auditLog.setAgentId(rs.getLong("agent_id"));
        auditLog.setAction(rs.getString("action"));
        auditLog.setEntity(rs.getString("entity"));
        auditLog.setEntityId(rs.getString("entity_id"));
        auditLog.setDetails(rs.getString("details"));
        auditLog.setIpAddress(rs.getString("ip_address"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            auditLog.setCreatedAt(createdAt.toLocalDateTime());
        }
        return auditLog;
    };

    @Override
    public Optional<AuditLog> findById(Long id) {
        String sql = "SELECT * FROM audit_logs WHERE id = ?";
        try {
            AuditLog auditLog = jdbcTemplate.queryForObject(sql, auditLogRowMapper, id);
            return Optional.of(auditLog);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<AuditLog> findByAgentId(Long agentId) {
        String sql = "SELECT * FROM audit_logs WHERE agent_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, auditLogRowMapper, agentId);
    }

    @Override
    public List<AuditLog> findAll() {
        String sql = "SELECT * FROM audit_logs ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, auditLogRowMapper);
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        String sql = "INSERT INTO audit_logs (agent_id, action, entity, entity_id, details, ip_address, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
            auditLog.getAgentId(),
            auditLog.getAction(),
            auditLog.getEntity(),
            auditLog.getEntityId(),
            auditLog.getDetails(),
            auditLog.getIpAddress(),
            Timestamp.valueOf(LocalDateTime.now())
        );

        Long id = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
        auditLog.setId(id);
        auditLog.setCreatedAt(LocalDateTime.now());
        
        return auditLog;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM audit_logs WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
