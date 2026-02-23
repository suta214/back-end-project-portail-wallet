package ma.hps.portailagent.security;

import ma.hps.portailagent.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AgentDetailsService implements UserDetailsService {
    @Autowired
    private AgentRepository agentRepository;

    @Override
    public AgentDetails loadUserByUsername(String agentId) throws UsernameNotFoundException {
        return agentRepository.findByAgentCode(agentId)
                .map(agent -> new AgentDetails(
                        agent.getId(),
                        agent.getAgentCode(),
                        agent.getPasswordHash(),
                        agent.getEmail(),
                        agent.isEnabled(),
                        agent.getPrivileges()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Agent not found with code: " + agentId));
    }
}
