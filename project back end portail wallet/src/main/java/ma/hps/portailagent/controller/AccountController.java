package ma.hps.portailagent.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AccountController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAccounts(
            @RequestParam(required = false) String clientId,
            @RequestParam(required = false) String phone
    ) {
        log.info("Fetching accounts for clientId: {}, phone: {}", clientId, phone);
        
        Map<String, Object> account = new HashMap<>();
        account.put("rib", "0010000000000000000000001");
        account.put("accountId", "ACC-0001");
        account.put("accountType", "Personnel");
        account.put("balance", 50000.00);
        
        return ResponseEntity.ok(Map.of("data", java.util.List.of(account)));
    }
}
