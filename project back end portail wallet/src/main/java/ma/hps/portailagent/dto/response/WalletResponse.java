package ma.hps.portailagent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private Long id;
    private String walletId;
    private String ownerName;
    private String phone;
    private String type;
    private String status;
    private BigDecimal balance;
    private BigDecimal dailyLimit;
    private String currency;
}
