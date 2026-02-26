package ma.hps.portailagent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    // Champs de base
    private BigDecimal agentBalance;
    private BigDecimal commissionBalance;
    private int todayTransactionsCount;
    private String agentName;
    private String agentCode;
    private String agentType;

    // Transactions du jour (par type)
    private long todayCashInCount;
    private BigDecimal todayCashInAmount;
    private long todayCashOutCount;
    private BigDecimal todayCashOutAmount;
    private long todayTransfersCount;
    private long todayBillPaymentsCount;

    // Gestion (Back Office)
    private long activeClients;
    private long totalWallets;
    private long pendingTransactions;

    // Admin global
    private long totalAgents;
    private BigDecimal totalVolume;
    private BigDecimal globalBalance;
}
