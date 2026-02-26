package ma.hps.portailagent.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.LoginRequest;
import ma.hps.portailagent.dto.request.PasswordChangeRequest;
import ma.hps.portailagent.dto.response.LoginResponse;
import ma.hps.portailagent.security.AgentDetails;
import ma.hps.portailagent.service.AuthService;
import ma.hps.portailagent.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private ProfileService profileService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for agent: {}", request.getAgentId());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestHeader("Authorization") String token) {
        log.info("Token refresh requested");
        String newToken = authService.refreshToken(token);
        return ResponseEntity.ok(Map.of("token", newToken));
    }

    @PostMapping("/force-change-password")
    public ResponseEntity<Map<String, String>> forceChangePassword(
            Authentication auth,
            @Valid @RequestBody PasswordChangeRequest request) {
        AgentDetails agent = (AgentDetails) auth.getPrincipal();
        log.info("Force password change for agent: {}", agent.getUsername());
        profileService.forceChangePassword(agent.getUsername(), request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
