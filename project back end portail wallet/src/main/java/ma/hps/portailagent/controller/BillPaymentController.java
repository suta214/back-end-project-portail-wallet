package ma.hps.portailagent.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.BillPaymentRequest;
import ma.hps.portailagent.dto.response.TransactionResponse;
import ma.hps.portailagent.security.AgentDetails;
import ma.hps.portailagent.service.BillPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments/bills")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
@PreAuthorize("hasAnyRole('BILL_PAYMENT')")
public class BillPaymentController {
    @Autowired
    private BillPaymentService billPaymentService;

    @PostMapping
    public ResponseEntity<TransactionResponse> payBill(@Valid @RequestBody BillPaymentRequest request, Authentication auth) {
        log.info("Bill payment for biller: {}", request.getBillerId());
        AgentDetails agent = (AgentDetails) auth.getPrincipal();
        TransactionResponse response = billPaymentService.payBill(agent.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
