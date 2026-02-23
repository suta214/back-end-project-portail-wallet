package ma.hps.portailagent.repository.impl;

import ma.hps.portailagent.enums.Privilege;
import ma.hps.portailagent.model.Agent;
import ma.hps.portailagent.repository.AgentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AgentRepositoryImpl implements AgentRepository {
    private final JdbcTemplate jdbcTemplate;

    public AgentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("unchecked")
    private final RowMapper<Agent> agentRowMapper = (rs, rowNum) -> {
        Agent agent = new Agent();
        agent.setId(rs.getLong("id"));
        agent.setAgentCode(rs.getString("agent_code"));
        agent.setFullName(rs.getString("full_name"));
        agent.setEmail(rs.getString("email"));
        agent.setPhone(rs.getString("phone"));
        agent.setPasswordHash(rs.getString("password_hash"));
        agent.setAgentType(Enum.valueOf(ma.hps.portailagent.enums.AgentType.class, rs.getString("agent_type")));
        agent.setEnabled(rs.getBoolean("enabled"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            agent.setCreatedAt(createdAt.toLocalDateTime());
        }
        agent.setPrivileges(findPrivilegesByAgentId(agent.getId()));
        return agent;
    };

    @Override
    public Optional<Agent> findById(Long id) {
        String sql = "SELECT * FROM agents WHERE id = ?";
        try {
            Agent agent = jdbcTemplate.queryForObject(sql, agentRowMapper, id);
            return Optional.of(agent);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Agent> findByAgentCode(String agentCode) {
        String sql = "SELECT * FROM agents WHERE agent_code = ?";
        try {
            Agent agent = jdbcTemplate.queryForObject(sql, agentRowMapper, agentCode);
            return Optional.of(agent);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Agent> findByEmail(String email) {
        String sql = "SELECT * FROM agents WHERE email = ?";
        try {
            Agent agent = jdbcTemplate.queryForObject(sql, agentRowMapper, email);
            return Optional.of(agent);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Agent> findAll() {
        String sql = "SELECT * FROM agents";
        return jdbcTemplate.query(sql, agentRowMapper);
    }

    @Override
    public Agent save(Agent agent) {
        String sql = "INSERT INTO agents (agent_code, full_name, email, phone, password_hash, agent_type, enabled, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql, 
            agent.getAgentCode(),
            agent.getFullName(),
            agent.getEmail(),
            agent.getPhone(),
            agent.getPasswordHash(),
            agent.getAgentType().toString(),
            agent.isEnabled(),
            Timestamp.valueOf(LocalDateTime.now())
        );

        // Get the generated id
        Long id = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
        agent.setId(id);
        agent.setCreatedAt(LocalDateTime.now());
        
        return agent;
    }

    @Override
    public Agent update(Agent agent) {
        String sql = "UPDATE agents SET full_name = ?, email = ?, phone = ?, password_hash = ?, agent_type = ?, enabled = ?, updated_at = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
            agent.getFullName(),
            agent.getEmail(),
            agent.getPhone(),
            agent.getPasswordHash(),
            agent.getAgentType().toString(),
            agent.isEnabled(),
            Timestamp.valueOf(LocalDateTime.now()),
            agent.getId()
        );
        
        return agent;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM agents WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Privilege> findPrivilegesByAgentId(Long agentId) {
        String sql = "SELECT privilege FROM agent_privileges WHERE agent_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Privilege.valueOf(rs.getString("privilege")), agentId);
    }

    @Override
    public void addPrivilege(Long agentId, Privilege privilege) {
        String sql = "INSERT OR IGNORE INTO agent_privileges (agent_id, privilege) VALUES (?, ?)";
        jdbcTemplate.update(sql, agentId, privilege.toString());
    }

    @Override
    public void removePrivilege(Long agentId, Privilege privilege) {
        String sql = "DELETE FROM agent_privileges WHERE agent_id = ? AND privilege = ?";
        jdbcTemplate.update(sql, agentId, privilege.toString());
    }
}
