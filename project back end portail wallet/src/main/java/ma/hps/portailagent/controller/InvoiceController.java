package ma.hps.portailagent.controller;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.security.AgentDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/invoices")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class InvoiceController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getInvoice(
            @RequestParam String billerId,
            @RequestParam String customerRef,
            @RequestParam(required = false) String contractNumber
    ) {
        log.info("Fetching invoice for biller: {}, customer: {}", billerId, customerRef);
        
        Map<String, Object> invoice = new HashMap<>();
        invoice.put("invoiceId", "INV-" + System.nanoTime());
        invoice.put("billerName", "Sample Biller");
        invoice.put("customerRef", customerRef);
        invoice.put("amount", 150.00);
        invoice.put("dueDate", "2026-03-31");
        invoice.put("period", "February 2026");
        
        return ResponseEntity.ok(invoice);
    }
}
