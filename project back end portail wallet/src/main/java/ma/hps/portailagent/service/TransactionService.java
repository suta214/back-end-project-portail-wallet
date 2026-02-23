package ma.hps.portailagent.service;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.CashInRequest;
import ma.hps.portailagent.dto.request.CashOutRequest;
import ma.hps.portailagent.dto.response.PageResponse;
import ma.hps.portailagent.dto.response.TransactionResponse;
import ma.hps.portailagent.enums.TransactionStatus;
import ma.hps.portailagent.exception.ResourceNotFoundException;
import ma.hps.portailagent.model.TransactionLog;
import ma.hps.portailagent.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public TransactionResponse cashIn(Long agentId, CashInRequest request) {
        TransactionLog transaction = new TransactionLog();
        transaction.setTransactionId("CASHIN-" + System.currentTimeMillis());
        transaction.setType("Cash In");
        transaction.setAgentId(agentId);
        transaction.setClientName(request.getClientPhone());
        transaction.setAmount(request.getAmount());
        transaction.setFees(request.getFees() != null ? request.getFees() : BigDecimal.ZERO);
        transaction.setStatus(TransactionStatus.EN_ATTENTE);
        transaction.setReference("REF-" + System.nanoTime());
        transaction.setCreatedAt(LocalDateTime.now());

        TransactionLog saved = transactionRepository.save(transaction);
        
        return new TransactionResponse(
                saved.getTransactionId(),
                saved.getStatus().toString(),
                "Cash in initiated successfully"
        );
    }

    public TransactionResponse cashOut(Long agentId, CashOutRequest request) {
        TransactionLog transaction = new TransactionLog();
        transaction.setTransactionId("CASHOUT-" + System.currentTimeMillis());
        transaction.setType("Cash Out");
        transaction.setAgentId(agentId);
        transaction.setClientName(request.getClientPhone());
        transaction.setAmount(request.getAmount());
        transaction.setFees(request.getFees() != null ? request.getFees() : BigDecimal.ZERO);
        transaction.setStatus(TransactionStatus.EN_ATTENTE);
        transaction.setReference("REF-" + System.nanoTime());
        transaction.setCreatedAt(LocalDateTime.now());

        TransactionLog saved = transactionRepository.save(transaction);
        
        return new TransactionResponse(
                saved.getTransactionId(),
                saved.getStatus().toString(),
                "Cash out initiated successfully"
        );
    }

    public PageResponse<TransactionLog> getTransactions(Long agentId, int page, int pageSize) {
        List<TransactionLog> transactions = transactionRepository.findByAgentIdPaginated(agentId, page, pageSize);
        long total = transactionRepository.countByAgentId(agentId);
        long totalPages = (total + pageSize - 1) / pageSize;

        return PageResponse.<TransactionLog>builder()
                .data(transactions)
                .total(total)
                .page(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }
}
