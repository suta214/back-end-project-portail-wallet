package ma.hps.portailagent.service;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.PasswordChangeRequest;
import ma.hps.portailagent.dto.request.ProfileSettingsRequest;
import ma.hps.portailagent.dto.response.AgentProfile;
import ma.hps.portailagent.dto.response.DashboardStats;
import ma.hps.portailagent.enums.AgentType;
import ma.hps.portailagent.exception.ResourceNotFoundException;
import ma.hps.portailagent.model.Agent;
import ma.hps.portailagent.repository.AgentRepository;
import ma.hps.portailagent.repository.ClientRepository;
import ma.hps.portailagent.repository.TransactionRepository;
import ma.hps.portailagent.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class ProfileService {
    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AgentProfile getProfile(String agentCode) {
        Agent agent = agentRepository.findByAgentCode(agentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        AgentProfile profile = new AgentProfile();
        profile.setId(agent.getId());
        profile.setAgentId(agent.getAgentCode());
        profile.setAgentCode(agent.getAgentCode());
        profile.setFullName(agent.getFullName());
        profile.setEmail(agent.getEmail());
        profile.setPhone(agent.getPhone());
        profile.setLanguage("fr");
        profile.setTimezone("Africa/Casablanca");
        profile.setNotificationsEnabled(true);
        profile.setTwoFactorEnabled(false);
        profile.setBalance(BigDecimal.valueOf(50000.00));
        profile.setCommissionBalance(BigDecimal.valueOf(5000.00));
        profile.setTodayTransactionsCount((int) transactionRepository.countByAgentId(agent.getId()));
        
        return profile;
    }

    public DashboardStats getDashboardStats(String agentCode) {
        Agent agent = agentRepository.findByAgentCode(agentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        DashboardStats stats = new DashboardStats();
        stats.setAgentName(agent.getFullName());
        stats.setAgentCode(agent.getAgentCode());
        stats.setAgentType(agent.getAgentType().toString());
        stats.setAgentBalance(BigDecimal.valueOf(50000.00));
        stats.setCommissionBalance(BigDecimal.valueOf(5000.00));
        stats.setTodayTransactionsCount((int) transactionRepository.countByAgentId(agent.getId()));

        AgentType type = agent.getAgentType();

        if (type == AgentType.ADMIN) {
            // Admin : statistiques globales
            stats.setTotalAgents(agentRepository.countAll());
            stats.setActiveClients(clientRepository.countActive());
            stats.setTotalWallets(walletRepository.countAll());
            stats.setGlobalBalance(walletRepository.sumAllBalance());
            stats.setTotalVolume(transactionRepository.sumAllAmountToday());
            stats.setPendingTransactions(transactionRepository.countPending());
            stats.setTodayCashInCount(transactionRepository.countAllToday());
            stats.setTodayCashInAmount(transactionRepository.sumAllAmountToday());

        } else if (type == AgentType.BACK_OFFICE) {
            // Back Office : gestion clients et wallets
            stats.setActiveClients(clientRepository.countActive());
            stats.setTotalWallets(walletRepository.countAll());
            stats.setPendingTransactions(transactionRepository.countPending());
            stats.setGlobalBalance(walletRepository.sumAllBalance());

        } else if (type == AgentType.AGENT_PROPRE || type == AgentType.AGENT_MANDATE_PRINCIPAL) {
            // Agent Propre & Mandaté Principal : transactions complètes du jour
            Long agentId = agent.getId();
            stats.setTodayCashInCount(transactionRepository.countTodayByAgentIdAndType(agentId, "CASH_IN"));
            stats.setTodayCashInAmount(transactionRepository.sumTodayAmountByAgentIdAndType(agentId, "CASH_IN"));
            stats.setTodayCashOutCount(transactionRepository.countTodayByAgentIdAndType(agentId, "CASH_OUT"));
            stats.setTodayCashOutAmount(transactionRepository.sumTodayAmountByAgentIdAndType(agentId, "CASH_OUT"));
            stats.setTodayTransfersCount(transactionRepository.countTodayByAgentIdAndType(agentId, "TRANSFER"));
            stats.setTodayBillPaymentsCount(transactionRepository.countTodayByAgentIdAndType(agentId, "BILL_PAYMENT"));
            if (type == AgentType.AGENT_MANDATE_PRINCIPAL) {
                stats.setActiveClients(clientRepository.countActive());
                stats.setTotalWallets(walletRepository.countAll());
            }

        } else {
            // Mandaté Commerçant & Détaillant : Cash In/Out uniquement
            Long agentId = agent.getId();
            stats.setTodayCashInCount(transactionRepository.countTodayByAgentIdAndType(agentId, "CASH_IN"));
            stats.setTodayCashInAmount(transactionRepository.sumTodayAmountByAgentIdAndType(agentId, "CASH_IN"));
            stats.setTodayCashOutCount(transactionRepository.countTodayByAgentIdAndType(agentId, "CASH_OUT"));
            stats.setTodayCashOutAmount(transactionRepository.sumTodayAmountByAgentIdAndType(agentId, "CASH_OUT"));
        }

        return stats;
    }

    public AgentProfile updateProfile(String agentCode, AgentProfile request) {
        Agent agent = agentRepository.findByAgentCode(agentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        if (request.getEmail() != null) {
            agent.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            agent.setPhone(request.getPhone());
        }
        if (request.getFullName() != null) {
            agent.setFullName(request.getFullName());
        }

        agentRepository.update(agent);
        return getProfile(agentCode);
    }

    public void changePassword(String agentCode, PasswordChangeRequest request) {
        Agent agent = agentRepository.findByAgentCode(agentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), agent.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        agent.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        agent.setMustChangePassword(false);
        agentRepository.update(agent);
        log.info("Password changed for agent: {}", agentCode);
    }

    public void forceChangePassword(String agentCode, PasswordChangeRequest request) {
        Agent agent = agentRepository.findByAgentCode(agentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), agent.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        agent.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        agent.setMustChangePassword(false);
        agentRepository.update(agent);
        log.info("First-login password changed for agent: {}", agentCode);
    }

    public void updateSettings(String agentCode, ProfileSettingsRequest request) {
        Agent agent = agentRepository.findByAgentCode(agentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        // Note: Settings would be stored in a separate table in production
        // For now, we just log the update
        log.info("Settings updated for agent: {}", agentCode);
    }
}
