package ma.hps.portailagent.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.PasswordChangeRequest;
import ma.hps.portailagent.dto.request.ProfileSettingsRequest;
import ma.hps.portailagent.dto.response.AgentProfile;
import ma.hps.portailagent.dto.response.DashboardStats;
import ma.hps.portailagent.security.AgentDetails;
import ma.hps.portailagent.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/profile")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
@PreAuthorize("hasAnyRole('PROFILE')")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @GetMapping
    public ResponseEntity<AgentProfile> getProfile(Authentication auth) {
        log.info("Fetching profile");
        AgentDetails agent = (AgentDetails) auth.getPrincipal();
        AgentProfile profile = profileService.getProfile(agent.getUsername());
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<AgentProfile> updateProfile(Authentication auth, @Valid @RequestBody AgentProfile request) {
        log.info("Updating profile");
        AgentDetails agent = (AgentDetails) auth.getPrincipal();
        AgentProfile profile = profileService.updateProfile(agent.getUsername(), request);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(Authentication auth, @Valid @RequestBody PasswordChangeRequest request) {
        log.info("Changing password");
        AgentDetails agent = (AgentDetails) auth.getPrincipal();
        profileService.changePassword(agent.getUsername(), request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @PutMapping("/settings")
    public ResponseEntity<Map<String, String>> updateSettings(Authentication auth, @Valid @RequestBody ProfileSettingsRequest request) {
        log.info("Updating settings");
        AgentDetails agent = (AgentDetails) auth.getPrincipal();
        profileService.updateSettings(agent.getUsername(), request);
        return ResponseEntity.ok(Map.of("message", "Settings updated successfully"));
    }
}
