package ma.hps.portailagent.service;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.CreateAgentRequest;
import ma.hps.portailagent.dto.response.CreatedAgent;
import ma.hps.portailagent.exception.ResourceNotFoundException;
import ma.hps.portailagent.model.Agent;
import ma.hps.portailagent.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AgentService {
    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CreatedAgent createAgent(CreateAgentRequest request) {
        // Check if agent code already exists
        Optional<Agent> existing = agentRepository.findByAgentCode(request.getIdentifiant());
        if (existing.isPresent()) {
            throw new RuntimeException("Agent with code already exists");
        }

        Agent agent = new Agent();
        agent.setAgentCode(request.getIdentifiant());
        agent.setFullName(request.getFirstName() + " " + request.getLastName());
        agent.setEmail(request.getEmail());
        agent.setPhone(request.getPhone());
        agent.setPasswordHash(passwordEncoder.encode("DefaultPassword123"));
        agent.setAgentType(request.getAgentType());
        agent.setEnabled(true);
        agent.setCreatedAt(LocalDateTime.now());

        Agent saved = agentRepository.save(agent);

        CreatedAgent response = new CreatedAgent();
        response.setId(saved.getId());
        response.setIdentifiant(saved.getAgentCode());
        response.setFirstName(request.getFirstName());
        response.setLastName(request.getLastName());
        response.setAgentType(request.getAgentType());
        response.setEmail(request.getEmail());
        response.setPhone(request.getPhone());
        response.setStatus("Actif");
        response.setCreatedAt(LocalDateTime.now().toString());

        return response;
    }
}
