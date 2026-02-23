package ma.hps.portailagent.repository.impl;

import ma.hps.portailagent.model.Transfer;
import ma.hps.portailagent.repository.TransferRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TransferRepositoryImpl implements TransferRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransferRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Transfer> transferRowMapper = (rs, rowNum) -> {
        Transfer transfer = new Transfer();
        transfer.setId(rs.getLong("id"));
        transfer.setTransferId(rs.getString("transfer_id"));
        transfer.setAgentId(rs.getLong("agent_id"));
        transfer.setSenderWalletId(rs.getString("sender_wallet_id"));
        transfer.setReceiverWalletId(rs.getString("receiver_wallet_id"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        transfer.setFees(rs.getBigDecimal("fees"));
        transfer.setMotif(rs.getString("motif"));
        transfer.setStatus(ma.hps.portailagent.enums.TransactionStatus.valueOf(rs.getString("status")));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            transfer.setCreatedAt(createdAt.toLocalDateTime());
        }
        return transfer;
    };

    @Override
    public Optional<Transfer> findById(Long id) {
        String sql = "SELECT * FROM transfers WHERE id = ?";
        try {
            Transfer transfer = jdbcTemplate.queryForObject(sql, transferRowMapper, id);
            return Optional.of(transfer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Transfer> findByTransferId(String transferId) {
        String sql = "SELECT * FROM transfers WHERE transfer_id = ?";
        try {
            Transfer transfer = jdbcTemplate.queryForObject(sql, transferRowMapper, transferId);
            return Optional.of(transfer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Transfer> findByAgentId(Long agentId) {
        String sql = "SELECT * FROM transfers WHERE agent_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transferRowMapper, agentId);
    }

    @Override
    public List<Transfer> findByStatus(String status) {
        String sql = "SELECT * FROM transfers WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transferRowMapper, status);
    }

    @Override
    public Transfer save(Transfer transfer) {
        String sql = "INSERT INTO transfers (transfer_id, agent_id, sender_wallet_id, receiver_wallet_id, amount, fees, motif, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
            transfer.getTransferId(),
            transfer.getAgentId(),
            transfer.getSenderWalletId(),
            transfer.getReceiverWalletId(),
            transfer.getAmount(),
            transfer.getFees(),
            transfer.getMotif(),
            transfer.getStatus().toString(),
            Timestamp.valueOf(LocalDateTime.now())
        );

        Long id = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
        transfer.setId(id);
        transfer.setCreatedAt(LocalDateTime.now());
        
        return transfer;
    }

    @Override
    public Transfer update(Transfer transfer) {
        String sql = "UPDATE transfers SET sender_wallet_id = ?, receiver_wallet_id = ?, amount = ?, fees = ?, motif = ?, status = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
            transfer.getSenderWalletId(),
            transfer.getReceiverWalletId(),
            transfer.getAmount(),
            transfer.getFees(),
            transfer.getMotif(),
            transfer.getStatus().toString(),
            transfer.getId()
        );
        
        return transfer;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM transfers WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
