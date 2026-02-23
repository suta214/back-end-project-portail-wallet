package ma.hps.portailagent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentProfile {
    private Long id;
    private String agentId;
    private String agentCode;
    private String fullName;
    private String email;
    private String phone;
    private String language;
    private String timezone;
    private boolean notificationsEnabled;
    private boolean twoFactorEnabled;
    private BigDecimal balance;
    private BigDecimal commissionBalance;
    private int todayTransactionsCount;
}
