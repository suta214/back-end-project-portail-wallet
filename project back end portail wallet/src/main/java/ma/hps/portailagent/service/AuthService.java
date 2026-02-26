package ma.hps.portailagent.service;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.LoginRequest;
import ma.hps.portailagent.dto.response.LoginResponse;
import ma.hps.portailagent.exception.ResourceNotFoundException;
import ma.hps.portailagent.model.Agent;
import ma.hps.portailagent.repository.AgentRepository;
import ma.hps.portailagent.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Agent agent = agentRepository.findByAgentCode(request.getAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        if (!agent.isEnabled()) {
            throw new ResourceNotFoundException("Agent is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), agent.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", agent.getEmail());
        claims.put("fullName", agent.getFullName());
        claims.put("agentType", agent.getAgentType().toString());
        
        List<String> privileges = agent.getPrivileges().stream()
                .map(Enum::toString)
                .collect(Collectors.toList());
        claims.put("privileges", privileges);

        String token = jwtUtil.generateToken(agent.getAgentCode(), claims);

        LoginResponse response = new LoginResponse(
                token,
                agent.getAgentCode(),
                agent.getFullName(),
                agent.getAgentCode(),
                agent.getAgentType().toString(),
                privileges,
                agent.isMustChangePassword()
        );
        return response;
    }

    public String refreshToken(String token) {
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String agentId = jwtUtil.getAgentIdFromToken(token);
        Agent agent = agentRepository.findByAgentCode(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", agent.getEmail());
        claims.put("fullName", agent.getFullName());
        claims.put("agentType", agent.getAgentType().toString());
        
        List<String> privileges = agent.getPrivileges().stream()
                .map(Enum::toString)
                .collect(Collectors.toList());
        claims.put("privileges", privileges);

        return jwtUtil.generateToken(agent.getAgentCode(), claims);
    }
}
