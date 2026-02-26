package ma.hps.portailagent.repository.impl;

import ma.hps.portailagent.model.TransactionLog;
import ma.hps.portailagent.repository.TransactionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransactionRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<TransactionLog> transactionRowMapper = (rs, rowNum) -> {
        TransactionLog transaction = new TransactionLog();
        transaction.setId(rs.getLong("id"));
        transaction.setTransactionId(rs.getString("transaction_id"));
        transaction.setType(rs.getString("type"));
        transaction.setAgentId(rs.getLong("agent_id"));
        transaction.setClientName(rs.getString("client_name"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setFees(rs.getBigDecimal("fees"));
        transaction.setStatus(ma.hps.portailagent.enums.TransactionStatus.valueOf(rs.getString("status")));
        transaction.setReference(rs.getString("reference"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            transaction.setCreatedAt(createdAt.toLocalDateTime());
        }
        return transaction;
    };

    @Override
    public Optional<TransactionLog> findById(Long id) {
        String sql = "SELECT * FROM transaction_logs WHERE id = ?";
        try {
            TransactionLog transaction = jdbcTemplate.queryForObject(sql, transactionRowMapper, id);
            return Optional.of(transaction);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<TransactionLog> findByTransactionId(String transactionId) {
        String sql = "SELECT * FROM transaction_logs WHERE transaction_id = ?";
        try {
            TransactionLog transaction = jdbcTemplate.queryForObject(sql, transactionRowMapper, transactionId);
            return Optional.of(transaction);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<TransactionLog> findAll() {
        String sql = "SELECT * FROM transaction_logs ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper);
    }

    @Override
    public List<TransactionLog> findByAgentId(Long agentId) {
        String sql = "SELECT * FROM transaction_logs WHERE agent_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, agentId);
    }

    @Override
    public List<TransactionLog> findByStatus(String status) {
        String sql = "SELECT * FROM transaction_logs WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, status);
    }

    @Override
    public List<TransactionLog> findByType(String type) {
        String sql = "SELECT * FROM transaction_logs WHERE type = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, type);
    }

    @Override
    public List<TransactionLog> search(String searchTerm) {
        String sql = "SELECT * FROM transaction_logs WHERE client_name LIKE ? OR transaction_id LIKE ? OR reference LIKE ? ORDER BY created_at DESC";
        String pattern = "%" + searchTerm + "%";
        return jdbcTemplate.query(sql, transactionRowMapper, pattern, pattern, pattern);
    }

    @Override
    public List<TransactionLog> findByAgentIdPaginated(Long agentId, int page, int pageSize) {
        String sql = "SELECT * FROM transaction_logs WHERE agent_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * pageSize;
        return jdbcTemplate.query(sql, transactionRowMapper, agentId, pageSize, offset);
    }

    @Override
    public long countByAgentId(Long agentId) {
        String sql = "SELECT COUNT(*) FROM transaction_logs WHERE agent_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, agentId);
        return count != null ? count : 0;
    }

    @Override
    public TransactionLog save(TransactionLog transaction) {
        String sql = "INSERT INTO transaction_logs (transaction_id, type, agent_id, client_name, amount, fees, status, reference, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
            transaction.getTransactionId(),
            transaction.getType(),
            transaction.getAgentId(),
            transaction.getClientName(),
            transaction.getAmount(),
            transaction.getFees(),
            transaction.getStatus().toString(),
            transaction.getReference(),
            Timestamp.valueOf(LocalDateTime.now())
        );

        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        transaction.setId(id);
        transaction.setCreatedAt(LocalDateTime.now());
        
        return transaction;
    }

    @Override
    public TransactionLog update(TransactionLog transaction) {
        String sql = "UPDATE transaction_logs SET type = ?, client_name = ?, amount = ?, fees = ?, status = ?, reference = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
            transaction.getType(),
            transaction.getClientName(),
            transaction.getAmount(),
            transaction.getFees(),
            transaction.getStatus().toString(),
            transaction.getReference(),
            transaction.getId()
        );
        
        return transaction;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM transaction_logs WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public long countTodayByAgentIdAndType(Long agentId, String type) {
        String sql = "SELECT COUNT(*) FROM transaction_logs WHERE agent_id = ? AND type = ? AND DATE(created_at) = CURDATE()";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, agentId, type);
        return count != null ? count : 0;
    }

    @Override
    public java.math.BigDecimal sumTodayAmountByAgentIdAndType(Long agentId, String type) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transaction_logs WHERE agent_id = ? AND type = ? AND DATE(created_at) = CURDATE()";
        java.math.BigDecimal sum = jdbcTemplate.queryForObject(sql, java.math.BigDecimal.class, agentId, type);
        return sum != null ? sum : java.math.BigDecimal.ZERO;
    }

    @Override
    public long countAllToday() {
        String sql = "SELECT COUNT(*) FROM transaction_logs WHERE DATE(created_at) = CURDATE()";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    @Override
    public java.math.BigDecimal sumAllAmountToday() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transaction_logs WHERE DATE(created_at) = CURDATE()";
        java.math.BigDecimal sum = jdbcTemplate.queryForObject(sql, java.math.BigDecimal.class);
        return sum != null ? sum : java.math.BigDecimal.ZERO;
    }

    @Override
    public long countPending() {
        String sql = "SELECT COUNT(*) FROM transaction_logs WHERE status = 'EN_ATTENTE'";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }
}
