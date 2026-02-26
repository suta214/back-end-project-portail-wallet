package ma.hps.portailagent.repository.impl;

import ma.hps.portailagent.model.Wallet;
import ma.hps.portailagent.repository.WalletRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class WalletRepositoryImpl implements WalletRepository {
    private final JdbcTemplate jdbcTemplate;

    public WalletRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Wallet> walletRowMapper = (rs, rowNum) -> {
        Wallet wallet = new Wallet();
        wallet.setId(rs.getLong("id"));
        wallet.setWalletId(rs.getString("wallet_id"));
        wallet.setOwnerName(rs.getString("owner_name"));
        wallet.setPhone(rs.getString("phone"));
        wallet.setType(rs.getString("type"));
        wallet.setStatus(ma.hps.portailagent.enums.WalletStatus.valueOf(rs.getString("status")));
        wallet.setBalance(rs.getBigDecimal("balance"));
        wallet.setDailyLimit(rs.getBigDecimal("daily_limit"));
        wallet.setCurrency(rs.getString("currency"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            wallet.setCreatedAt(createdAt.toLocalDateTime());
        }
        return wallet;
    };

    @Override
    public Optional<Wallet> findById(Long id) {
        String sql = "SELECT * FROM wallets WHERE id = ?";
        try {
            Wallet wallet = jdbcTemplate.queryForObject(sql, walletRowMapper, id);
            return Optional.of(wallet);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Wallet> findByWalletId(String walletId) {
        String sql = "SELECT * FROM wallets WHERE wallet_id = ?";
        try {
            Wallet wallet = jdbcTemplate.queryForObject(sql, walletRowMapper, walletId);
            return Optional.of(wallet);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Wallet> findByPhone(String phone) {
        String sql = "SELECT * FROM wallets WHERE phone = ?";
        try {
            Wallet wallet = jdbcTemplate.queryForObject(sql, walletRowMapper, phone);
            return Optional.of(wallet);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Wallet> findAll() {
        String sql = "SELECT * FROM wallets";
        return jdbcTemplate.query(sql, walletRowMapper);
    }

    @Override
    public List<Wallet> findByStatus(String status) {
        String sql = "SELECT * FROM wallets WHERE status = ?";
        return jdbcTemplate.query(sql, walletRowMapper, status);
    }

    @Override
    public List<Wallet> findByType(String type) {
        String sql = "SELECT * FROM wallets WHERE type = ?";
        return jdbcTemplate.query(sql, walletRowMapper, type);
    }

    @Override
    public List<Wallet> search(String searchTerm) {
        String sql = "SELECT * FROM wallets WHERE owner_name LIKE ? OR phone LIKE ? OR wallet_id LIKE ?";
        String pattern = "%" + searchTerm + "%";
        return jdbcTemplate.query(sql, walletRowMapper, pattern, pattern, pattern);
    }

    @Override
    public Wallet save(Wallet wallet) {
        String sql = "INSERT INTO wallets (wallet_id, owner_name, phone, type, status, balance, daily_limit, currency, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
            wallet.getWalletId(),
            wallet.getOwnerName(),
            wallet.getPhone(),
            wallet.getType(),
            wallet.getStatus().toString(),
            wallet.getBalance(),
            wallet.getDailyLimit(),
            wallet.getCurrency(),
            Timestamp.valueOf(LocalDateTime.now())
        );

        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        wallet.setId(id);
        wallet.setCreatedAt(LocalDateTime.now());
        
        return wallet;
    }

    @Override
    public Wallet update(Wallet wallet) {
        String sql = "UPDATE wallets SET owner_name = ?, phone = ?, type = ?, status = ?, balance = ?, daily_limit = ?, currency = ?, updated_at = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
            wallet.getOwnerName(),
            wallet.getPhone(),
            wallet.getType(),
            wallet.getStatus().toString(),
            wallet.getBalance(),
            wallet.getDailyLimit(),
            wallet.getCurrency(),
            Timestamp.valueOf(LocalDateTime.now()),
            wallet.getId()
        );
        
        return wallet;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM wallets WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM wallets WHERE status = 'ACTIF'";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    @Override
    public BigDecimal sumAllBalance() {
        String sql = "SELECT COALESCE(SUM(balance), 0) FROM wallets WHERE status = 'ACTIF'";
        BigDecimal sum = jdbcTemplate.queryForObject(sql, BigDecimal.class);
        return sum != null ? sum : BigDecimal.ZERO;
    }
}
