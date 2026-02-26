package ma.hps.portailagent.security;

import ma.hps.portailagent.enums.AgentType;
import ma.hps.portailagent.enums.Privilege;
import ma.hps.portailagent.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AgentDetailsService implements UserDetailsService {
    @Autowired
    private AgentRepository agentRepository;

    @Override
    public AgentDetails loadUserByUsername(String agentId) throws UsernameNotFoundException {
        return agentRepository.findByAgentCode(agentId)
                .map(agent -> {
                    // Un agent de type ALL reçoit automatiquement tous les privilèges
                    List<Privilege> privileges = (agent.getAgentType() == AgentType.ALL)
                            ? Arrays.asList(Privilege.values())
                            : agent.getPrivileges();
                    return new AgentDetails(
                            agent.getId(),
                            agent.getAgentCode(),
                            agent.getPasswordHash(),
                            agent.getEmail(),
                            agent.isEnabled(),
                            privileges
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("Agent not found with code: " + agentId));
    }
}
