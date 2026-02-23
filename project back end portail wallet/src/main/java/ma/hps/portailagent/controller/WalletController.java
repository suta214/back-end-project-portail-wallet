package ma.hps.portailagent.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.CreateWalletRequest;
import ma.hps.portailagent.dto.request.WalletAction;
import ma.hps.portailagent.dto.response.BeneficiaryVerification;
import ma.hps.portailagent.dto.response.WalletResponse;
import ma.hps.portailagent.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wallets")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
@PreAuthorize("hasAnyRole('WALLET_MGMT', 'ADMIN')")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @GetMapping
    public ResponseEntity<List<WalletResponse>> getWallets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search
    ) {
        log.info("Fetching wallets with status: {}, type: {}, search: {}", status, type, search);
        List<WalletResponse> wallets = walletService.getWallets(status, type, search);
        return ResponseEntity.ok(wallets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable Long id) {
        log.info("Fetching wallet with id: {}", id);
        WalletResponse wallet = walletService.getWalletById(id);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/verify")
    @PreAuthorize("permitAll()")
    public ResponseEntity<BeneficiaryVerification> verifyWallet(@RequestParam String phone) {
        log.info("Verifying wallet for phone: {}", phone);
        BeneficiaryVerification verification = walletService.verifyWallet(phone);
        return ResponseEntity.ok(verification);
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        log.info("Creating new wallet for: {}", request.getOwnerName());
        WalletResponse wallet = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

    @PostMapping("/{id}/actions")
    public ResponseEntity<Map<String, String>> applyWalletAction(@PathVariable Long id, @Valid @RequestBody WalletAction action) {
        log.info("Applying action {} to wallet {}", action.getAction(), id);
        walletService.applyAction(id, action);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Action applied successfully");
        return ResponseEntity.ok(response);
    }
}
