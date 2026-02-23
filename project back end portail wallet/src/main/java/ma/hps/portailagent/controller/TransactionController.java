package ma.hps.portailagent.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.CashInRequest;
import ma.hps.portailagent.dto.request.CashOutRequest;
import ma.hps.portailagent.dto.response.PageResponse;
import ma.hps.portailagent.dto.response.TransactionResponse;
import ma.hps.portailagent.model.TransactionLog;
import ma.hps.portailagent.security.AgentDetails;
import ma.hps.portailagent.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/cash-in")
    @PreAuthorize("hasAnyRole('CASH_IN')")
    public ResponseEntity<TransactionResponse> cashIn(@Valid @RequestBody CashInRequest request, Authentication auth) {
        log.info("Cash-in request: {}", request.getAmount());
        AgentDetails agent = (AgentDetails) auth.getPrincipal();
        TransactionResponse response = transactionService.cashIn(agent.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/cash-out")
    @PreAuthorize("hasAnyRole('CASH_OUT')")
    public ResponseEntity<TransactionResponse> cashOut(@Valid @RequestBody CashOutRequest request, Authentication auth) {
        log.info("Cash-out request: {}", request.getAmount());
        AgentDetails agent = (AgentDetails) auth.getPrincipal();
        TransactionResponse response = transactionService.cashOut(agent.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HISTORY')")
    public ResponseEntity<PageResponse<TransactionLog>> getTransactions(
            Authentication auth,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        log.info("Fetching transactions for page: {}", page);
        AgentDetails agent = (AgentDetails) auth.getPrincipal();
        PageResponse<TransactionLog> response = transactionService.getTransactions(agent.getId(), page, pageSize);
        return ResponseEntity.ok(response);
    }
}
