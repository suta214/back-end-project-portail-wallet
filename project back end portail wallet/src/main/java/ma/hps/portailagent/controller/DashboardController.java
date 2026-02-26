package ma.hps.portailagent.controller;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.response.DashboardStats;
import ma.hps.portailagent.security.AgentDetails;
import ma.hps.portailagent.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class DashboardController {
    @Autowired
    private ProfileService profileService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getDashboardStats(Authentication auth) {
        log.info("Fetching dashboard stats");
        AgentDetails agent = (AgentDetails) auth.getPrincipal();
        DashboardStats stats = profileService.getDashboardStats(agent.getUsername());
        return ResponseEntity.ok(stats);
    }
}
