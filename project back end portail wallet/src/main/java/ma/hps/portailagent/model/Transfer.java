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
public class Transfer {
    private Long id;
    private String transferId;
    private Long agentId;
    private String senderWalletId;
    private String receiverWalletId;
    private BigDecimal amount;
    private BigDecimal fees;
    private String motif;
    private TransactionStatus status;
    private LocalDateTime createdAt;
}
