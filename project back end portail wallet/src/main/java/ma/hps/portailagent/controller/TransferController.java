package ma.hps.portailagent.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.TransferRequest;
import ma.hps.portailagent.dto.response.TransactionResponse;
import ma.hps.portailagent.security.AgentDetails;
import ma.hps.portailagent.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfers")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
@PreAuthorize("hasAnyRole('TRANSFER')")
public class TransferController {
    @Autowired
    private TransferService transferService;

    @PostMapping
    public ResponseEntity<TransactionResponse> initiateTransfer(@Valid @RequestBody TransferRequest request, Authentication auth) {
        log.info("Transfer initiation: {} to {}", request.getSenderPhone(), request.getBeneficiaryPhone());
        AgentDetails agent = (AgentDetails) auth.getPrincipal();
        TransactionResponse response = transferService.initiateTransfer(agent.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
