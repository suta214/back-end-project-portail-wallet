package ma.hps.portailagent.repository;

import ma.hps.portailagent.enums.Privilege;
import ma.hps.portailagent.model.Agent;

import java.util.List;
import java.util.Optional;

public interface AgentRepository {
    Optional<Agent> findById(Long id);
    Optional<Agent> findByAgentCode(String agentCode);
    Optional<Agent> findByEmail(String email);
    List<Agent> findAll();
    Agent save(Agent agent);
    Agent update(Agent agent);
    void delete(Long id);
    List<Privilege> findPrivilegesByAgentId(Long agentId);
    void addPrivilege(Long agentId, Privilege privilege);
    void removePrivilege(Long agentId, Privilege privilege);
}
