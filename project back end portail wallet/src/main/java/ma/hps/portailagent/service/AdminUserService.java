package ma.hps.portailagent.service;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.UpdateAddressRequest;
import ma.hps.portailagent.dto.request.UpdateContractRequest;
import ma.hps.portailagent.dto.request.UpdateUserRequest;
import ma.hps.portailagent.dto.response.AdminUserResponse;
import ma.hps.portailagent.enums.AgentType;
import ma.hps.portailagent.enums.Privilege;
import ma.hps.portailagent.exception.ResourceNotFoundException;
import ma.hps.portailagent.model.Agent;
import ma.hps.portailagent.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminUserService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getUsers(String search, String status, int page, int pageSize) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.*, GROUP_CONCAT(ap.privilege) AS privileges FROM agents a " +
                "LEFT JOIN agent_privileges ap ON a.id = ap.agent_id WHERE 1=1");

        if (search != null && !search.isBlank()) {
            sql.append(" AND (a.agent_code LIKE ? OR a.full_name LIKE ?)");
        }
        if (status != null && !status.isBlank()) {
            if ("ACTIVE".equals(status)) sql.append(" AND a.enabled = TRUE AND (a.status IS NULL OR a.status = 'ACTIVE')");
            else if ("INACTIVE".equals(status)) sql.append(" AND a.enabled = FALSE AND (a.status IS NULL OR a.status = 'INACTIVE')");
            else if ("LOCKED".equals(status)) sql.append(" AND a.status = 'LOCKED'");
        }
        sql.append(" GROUP BY a.id ORDER BY a.created_at DESC LIMIT ? OFFSET ?");

        String pattern = search != null ? "%" + search + "%" : null;

        List<Object> params = new java.util.ArrayList<>();
        if (pattern != null) { params.add(pattern); params.add(pattern); }
        params.add(pageSize);
        params.add(page * pageSize);

        List<AdminUserResponse> data = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            AdminUserResponse r = new AdminUserResponse();
            r.setId(rs.getLong("id"));
            r.setIdentifiant(rs.getString("agent_code"));
            String fullName = rs.getString("full_name");
            String[] parts = fullName != null ? fullName.split(" ", 2) : new String[]{"", ""};
            r.setFirstName(parts.length > 0 ? parts[0] : "");
            r.setLastName(parts.length > 1 ? parts[1] : "");
            r.setEmail(rs.getString("email"));
            r.setPhone(rs.getString("phone"));
            r.setAgentType(AgentType.valueOf(rs.getString("agent_type")));
            r.setStatus(rs.getBoolean("enabled") ? "ACTIVE" : "INACTIVE");
            try { r.setStatus(rs.getString("status") != null ? rs.getString("status") : r.getStatus()); } catch (Exception ignored) {}
            r.setCreatedAt(rs.getString("created_at"));
            String privStr = rs.getString("privileges");
            if (privStr != null) {
                r.setPrivileges(Arrays.stream(privStr.split(","))
                        .map(p -> { try { return Privilege.valueOf(p.trim()); } catch (Exception e) { return null; } })
                        .filter(p -> p != null)
                        .collect(Collectors.toList()));
            }
            return r;
        }, params.toArray());

        // Count total
        String countSql = "SELECT COUNT(*) FROM agents a WHERE 1=1" +
                (pattern != null ? " AND (a.agent_code LIKE ? OR a.full_name LIKE ?)" : "");
        List<Object> countParams = pattern != null ? List.of(pattern, pattern) : List.of();
        long total = jdbcTemplate.queryForObject(countSql, Long.class, countParams.toArray());

        return Map.of("data", data, "total", total, "page", page, "pageSize", pageSize);
    }

    public AdminUserResponse getUserById(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable: " + id));
        return toResponse(agent);
    }

    public void updateUser(Long id, UpdateUserRequest req) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable: " + id));
        if (req.getFirstName() != null || req.getLastName() != null) {
            String fn = req.getFirstName() != null ? req.getFirstName() : agent.getFullName().split(" ", 2)[0];
            String ln = req.getLastName() != null ? req.getLastName() : (agent.getFullName().split(" ", 2).length > 1 ? agent.getFullName().split(" ", 2)[1] : "");
            agent.setFullName(fn + " " + ln);
        }
        if (req.getEmail() != null) agent.setEmail(req.getEmail());
        if (req.getPhone() != null) agent.setPhone(req.getPhone());
        agentRepository.update(agent);

        jdbcTemplate.update("UPDATE agents SET id_type=?, id_number=?, commission=? WHERE id=?",
                req.getIdType(), req.getIdNumber(), req.getCommission(), id);
    }

    public void updateContract(Long id, UpdateContractRequest req) {
        agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable: " + id));
        jdbcTemplate.update("UPDATE agents SET contract_type=?, contract_date=?, signatory=?, patent_number=? WHERE id=?",
                req.getContractType(),
                req.getContractDate() != null ? java.sql.Date.valueOf(req.getContractDate()) : null,
                req.getSignatory(), req.getPatentNumber(), id);
    }

    public void updateProfile(Long id, String agentTypeStr) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable: " + id));
        agent.setAgentType(AgentType.valueOf(agentTypeStr));
        agentRepository.update(agent);
    }

    public void updatePrivileges(Long id, List<String> privilegeStrings) {
        agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable: " + id));
        jdbcTemplate.update("DELETE FROM agent_privileges WHERE agent_id = ?", id);
        for (String p : privilegeStrings) {
            try {
                Privilege priv = Privilege.valueOf(p);
                agentRepository.addPrivilege(id, priv);
            } catch (IllegalArgumentException e) {
                log.warn("Privilege inconnu ignoré: {}", p);
            }
        }
    }

    public void lockUnlock(Long id, String action) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable: " + id));
        boolean unlock = "unlock".equalsIgnoreCase(action);
        agent.setEnabled(unlock);
        agentRepository.update(agent);
        String status = unlock ? "ACTIVE" : "LOCKED";
        jdbcTemplate.update("UPDATE agents SET status = ? WHERE id = ?", status, id);
    }

    public void updateAddress(Long id, UpdateAddressRequest req) {
        agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable: " + id));
        jdbcTemplate.update(
                "UPDATE agents SET address_line1=?, address_line2=?, address_line3=?, address_line4=?, country=?, region=?, city=?, postal_code=? WHERE id=?",
                req.getAddressLine1(), req.getAddressLine2(), req.getAddressLine3(), req.getAddressLine4(),
                req.getCountry(), req.getRegion(), req.getCity(), req.getPostalCode(), id);
    }

    public void deleteUser(Long id) {
        agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable: " + id));
        Agent agent = agentRepository.findById(id).get();
        agent.setEnabled(false);
        agentRepository.update(agent);
        jdbcTemplate.update("UPDATE agents SET status = 'INACTIVE' WHERE id = ?", id);
    }

    public List<Map<String, String>> getAvailableProfiles() {
        return List.of(
                Map.of("type", "ADMIN",                    "label", "Administrateur"),
                Map.of("type", "BACK_OFFICE",              "label", "Back Office"),
                Map.of("type", "AGENT_PROPRE",             "label", "Agent Propre"),
                Map.of("type", "AGENT_MANDATE_PRINCIPAL",  "label", "Mandaté Principal"),
                Map.of("type", "AGENT_MANDATE_COMMERCANT", "label", "Mandaté Commerçant"),
                Map.of("type", "AGENT_MANDATE_DETAILLANT", "label", "Mandaté Détaillant"),
                Map.of("type", "ALL",                      "label", "Tous les privilèges")
        );
    }

    private AdminUserResponse toResponse(Agent agent) {
        AdminUserResponse r = new AdminUserResponse();
        r.setId(agent.getId());
        r.setIdentifiant(agent.getAgentCode());
        String[] parts = agent.getFullName() != null ? agent.getFullName().split(" ", 2) : new String[]{"", ""};
        r.setFirstName(parts.length > 0 ? parts[0] : "");
        r.setLastName(parts.length > 1 ? parts[1] : "");
        r.setEmail(agent.getEmail());
        r.setPhone(agent.getPhone());
        r.setAgentType(agent.getAgentType());
        r.setStatus(agent.isEnabled() ? "ACTIVE" : "INACTIVE");
        r.setPrivileges(agent.getPrivileges());
        r.setCreatedAt(agent.getCreatedAt() != null ? agent.getCreatedAt().toString() : null);

        // Champs étendus via JDBC
        try {
            Map<String, Object> extra = jdbcTemplate.queryForMap(
                    "SELECT status, id_type, id_number, commission, rib, contract_type, contract_date, " +
                    "signatory, patent_number, address_line1, address_line2, address_line3, address_line4, " +
                    "country, region, city, postal_code FROM agents WHERE id = ?", agent.getId());
            if (extra.get("status") != null) r.setStatus((String) extra.get("status"));
            r.setIdType((String) extra.get("id_type"));
            r.setIdNumber((String) extra.get("id_number"));
            r.setCommission(extra.get("commission") != null ? new java.math.BigDecimal(extra.get("commission").toString()) : null);
            r.setRib((String) extra.get("rib"));
            r.setContractType((String) extra.get("contract_type"));
            if (extra.get("contract_date") != null) r.setContractDate(((java.sql.Date) extra.get("contract_date")).toLocalDate());
            r.setSignatory((String) extra.get("signatory"));
            r.setPatentNumber((String) extra.get("patent_number"));
            r.setAddressLine1((String) extra.get("address_line1"));
            r.setAddressLine2((String) extra.get("address_line2"));
            r.setAddressLine3((String) extra.get("address_line3"));
            r.setAddressLine4((String) extra.get("address_line4"));
            r.setCountry((String) extra.get("country"));
            r.setRegion((String) extra.get("region"));
            r.setCity((String) extra.get("city"));
            r.setPostalCode((String) extra.get("postal_code"));
        } catch (Exception e) {
            log.warn("Colonnes étendues non encore disponibles pour agent {}", agent.getId());
        }
        return r;
    }
}
