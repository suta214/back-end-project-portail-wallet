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
public class TransactionLog {
    private Long id;
    private String transactionId;
    private String type;
    private Long agentId;
    private String clientName;
    private BigDecimal amount;
    private BigDecimal fees;
    private TransactionStatus status;
    private String reference;
    private LocalDateTime createdAt;
}
