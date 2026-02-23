package ma.hps.portailagent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.hps.portailagent.enums.WalletStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    private Long id;
    private String walletId;
    private String ownerName;
    private String phone;
    private String type;
    private WalletStatus status;
    private BigDecimal balance;
    private BigDecimal dailyLimit;
    private String currency;
    private LocalDateTime createdAt;
}
