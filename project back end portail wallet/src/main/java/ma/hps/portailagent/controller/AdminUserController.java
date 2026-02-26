package ma.hps.portailagent.controller;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.UpdateAddressRequest;
import ma.hps.portailagent.dto.request.UpdateContractRequest;
import ma.hps.portailagent.dto.request.UpdateUserRequest;
import ma.hps.portailagent.dto.response.AdminUserResponse;
import ma.hps.portailagent.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "http://localhost:4300"})
@PreAuthorize("hasAuthority('ROLE_AGENT_MGMT')")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    /** GET /api/admin/users?search=&status=&page=0&pageSize=10 */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(adminUserService.getUsers(search, status, page, pageSize));
    }

    /** GET /api/admin/users/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getUserById(id));
    }

    /** PUT /api/admin/users/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id,
                                                          @RequestBody UpdateUserRequest req) {
        adminUserService.updateUser(id, req);
        return ResponseEntity.ok(Map.of("message", "Utilisateur mis à jour"));
    }

    /** PUT /api/admin/users/{id}/contract */
    @PutMapping("/{id}/contract")
    public ResponseEntity<Map<String, String>> updateContract(@PathVariable Long id,
                                                               @RequestBody UpdateContractRequest req) {
        adminUserService.updateContract(id, req);
        return ResponseEntity.ok(Map.of("message", "Contrat mis à jour"));
    }

    /** PUT /api/admin/users/{id}/profile */
    @PutMapping("/{id}/profile")
    public ResponseEntity<Map<String, String>> updateProfile(@PathVariable Long id,
                                                              @RequestBody Map<String, String> body) {
        adminUserService.updateProfile(id, body.get("agentType"));
        return ResponseEntity.ok(Map.of("message", "Profil mis à jour"));
    }

    /** PUT /api/admin/users/{id}/privileges */
    @PutMapping("/{id}/privileges")
    public ResponseEntity<Map<String, String>> updatePrivileges(@PathVariable Long id,
                                                                 @RequestBody Map<String, List<String>> body) {
        adminUserService.updatePrivileges(id, body.get("privileges"));
        return ResponseEntity.ok(Map.of("message", "Fonctionnalités mises à jour"));
    }

    /** PUT /api/admin/users/{id}/lock */
    @PutMapping("/{id}/lock")
    public ResponseEntity<Map<String, String>> lockUnlock(@PathVariable Long id,
                                                           @RequestBody Map<String, String> body) {
        adminUserService.lockUnlock(id, body.get("action"));
        return ResponseEntity.ok(Map.of("message", "Statut mis à jour"));
    }

    /** PUT /api/admin/users/{id}/address */
    @PutMapping("/{id}/address")
    public ResponseEntity<Map<String, String>> updateAddress(@PathVariable Long id,
                                                              @RequestBody UpdateAddressRequest req) {
        adminUserService.updateAddress(id, req);
        return ResponseEntity.ok(Map.of("message", "Adresse mise à jour"));
    }

    /** DELETE /api/admin/users/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé"));
    }

    /** GET /api/admin/profiles */
    @GetMapping("/profiles")
    public ResponseEntity<List<Map<String, String>>> getProfiles() {
        return ResponseEntity.ok(adminUserService.getAvailableProfiles());
    }
}
