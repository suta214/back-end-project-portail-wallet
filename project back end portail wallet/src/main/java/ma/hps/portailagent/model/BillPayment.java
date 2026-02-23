package ma.hps.portailagent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.hps.portailagent.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillPayment {
    private Long id;
    private String paymentId;
    private Long agentId;
    private String billerId;
    private String customerRef;
    private String contractNumber;
    private BigDecimal amount;
    private BigDecimal fees;
    private TransactionStatus status;
    private String invoiceRef;
    private LocalDateTime createdAt;
}
