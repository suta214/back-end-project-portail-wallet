package ma.hps.portailagent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private BigDecimal agentBalance;
    private BigDecimal commissionBalance;
    private int todayTransactionsCount;
    private String agentName;
    private String agentCode;
}
