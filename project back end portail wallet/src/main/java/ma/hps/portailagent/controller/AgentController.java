package ma.hps.portailagent.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.CreateAgentRequest;
import ma.hps.portailagent.dto.response.CreatedAgent;
import ma.hps.portailagent.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/agents")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
@PreAuthorize("hasAnyRole('AGENT_MGMT')")
public class AgentController {
    @Autowired
    private AgentService agentService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAgent(@Valid @RequestBody CreateAgentRequest request) {
        log.info("Creating new agent: {}", request.getIdentifiant());
        
        CreatedAgent agent = agentService.createAgent(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Agent created successfully");
        response.put("agent", agent);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAgents() {
        log.info("Fetching all agents");
        Map<String, Object> response = new HashMap<>();
        response.put("data", new java.util.ArrayList<>());
        response.put("total", 0);
        return ResponseEntity.ok(response);
    }
}
