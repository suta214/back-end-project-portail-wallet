package ma.hps.portailagent.repository.impl;

import ma.hps.portailagent.model.Client;
import ma.hps.portailagent.repository.ClientRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ClientRepositoryImpl implements ClientRepository {
    private final JdbcTemplate jdbcTemplate;

    public ClientRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Client> clientRowMapper = (rs, rowNum) -> {
        Client client = new Client();
        client.setId(rs.getLong("id"));
        client.setLastName(rs.getString("last_name"));
        client.setFirstName(rs.getString("first_name"));
        client.setCin(rs.getString("cin"));
        client.setPhone(rs.getString("phone"));
        client.setEmail(rs.getString("email"));
        client.setWalletId(rs.getString("wallet_id"));
        client.setStatus(rs.getString("status"));
        client.setKycVerified(rs.getBoolean("kyc_verified"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            client.setCreatedAt(createdAt.toLocalDateTime());
        }
        return client;
    };

    @Override
    public Optional<Client> findById(Long id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try {
            Client client = jdbcTemplate.queryForObject(sql, clientRowMapper, id);
            return Optional.of(client);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Client> findByCin(String cin) {
        String sql = "SELECT * FROM clients WHERE cin = ?";
        try {
            Client client = jdbcTemplate.queryForObject(sql, clientRowMapper, cin);
            return Optional.of(client);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Client> findByPhone(String phone) {
        String sql = "SELECT * FROM clients WHERE phone = ?";
        try {
            Client client = jdbcTemplate.queryForObject(sql, clientRowMapper, phone);
            return Optional.of(client);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        String sql = "SELECT * FROM clients WHERE email = ?";
        try {
            Client client = jdbcTemplate.queryForObject(sql, clientRowMapper, email);
            return Optional.of(client);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Client> findAll() {
        String sql = "SELECT * FROM clients";
        return jdbcTemplate.query(sql, clientRowMapper);
    }

    @Override
    public List<Client> findByStatus(String status) {
        String sql = "SELECT * FROM clients WHERE status = ?";
        return jdbcTemplate.query(sql, clientRowMapper, status);
    }

    @Override
    public List<Client> search(String searchTerm) {
        String sql = "SELECT * FROM clients WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? OR cin LIKE ?";
        String pattern = "%" + searchTerm + "%";
        return jdbcTemplate.query(sql, clientRowMapper, pattern, pattern, pattern, pattern);
    }

    @Override
    public Client save(Client client) {
        String sql = "INSERT INTO clients (last_name, first_name, cin, phone, email, wallet_id, status, kyc_verified, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
            client.getLastName(),
            client.getFirstName(),
            client.getCin(),
            client.getPhone(),
            client.getEmail(),
            client.getWalletId(),
            client.getStatus(),
            client.isKycVerified(),
            Timestamp.valueOf(LocalDateTime.now())
        );

        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        client.setId(id);
        client.setCreatedAt(LocalDateTime.now());
        
        return client;
    }

    @Override
    public Client update(Client client) {
        String sql = "UPDATE clients SET last_name = ?, first_name = ?, phone = ?, email = ?, wallet_id = ?, status = ?, kyc_verified = ?, updated_at = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
            client.getLastName(),
            client.getFirstName(),
            client.getPhone(),
            client.getEmail(),
            client.getWalletId(),
            client.getStatus(),
            client.isKycVerified(),
            Timestamp.valueOf(LocalDateTime.now()),
            client.getId()
        );
        
        return client;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public long countActive() {
        String sql = "SELECT COUNT(*) FROM clients WHERE status = 'Actif'";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM clients";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }
}
