package ma.hps.portailagent.service;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.BillPaymentRequest;
import ma.hps.portailagent.dto.response.TransactionResponse;
import ma.hps.portailagent.enums.TransactionStatus;
import ma.hps.portailagent.model.BillPayment;
import ma.hps.portailagent.repository.BillPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class BillPaymentService {
    @Autowired
    private BillPaymentRepository billPaymentRepository;

    public TransactionResponse payBill(Long agentId, BillPaymentRequest request) {
        BillPayment payment = new BillPayment();
        payment.setPaymentId("BILLPAY-" + System.currentTimeMillis());
        payment.setAgentId(agentId);
        payment.setBillerId(request.getBillerId());
        payment.setCustomerRef(request.getCustomerRef());
        payment.setContractNumber(request.getContractNumber());
        payment.setAmount(request.getAmount());
        payment.setFees(request.getFees() != null ? request.getFees() : BigDecimal.ZERO);
        payment.setStatus(TransactionStatus.EN_ATTENTE);
        payment.setInvoiceRef("INV-" + System.nanoTime());
        payment.setCreatedAt(LocalDateTime.now());

        BillPayment saved = billPaymentRepository.save(payment);
        
        return new TransactionResponse(
                saved.getPaymentId(),
                saved.getStatus().toString(),
                "Bill payment initiated successfully"
        );
    }
}
