package ma.hps.portailagent.service;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.TransferRequest;
import ma.hps.portailagent.dto.response.TransactionResponse;
import ma.hps.portailagent.enums.TransactionStatus;
import ma.hps.portailagent.exception.ResourceNotFoundException;
import ma.hps.portailagent.model.Transfer;
import ma.hps.portailagent.repository.TransferRepository;
import ma.hps.portailagent.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class TransferService {
    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private WalletRepository walletRepository;

    public TransactionResponse initiateTransfer(Long agentId, TransferRequest request) {
        String senderWalletId = walletRepository.findByPhone(request.getSenderPhone())
                .map(w -> w.getWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet introuvable pour le telephone expediteur: " + request.getSenderPhone()));

        String receiverWalletId = walletRepository.findByPhone(request.getBeneficiaryPhone())
                .map(w -> w.getWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet introuvable pour le telephone beneficiaire: " + request.getBeneficiaryPhone()));

        Transfer transfer = new Transfer();
        transfer.setTransferId("TRANSFER-" + System.currentTimeMillis());
        transfer.setAgentId(agentId);
        transfer.setSenderWalletId(senderWalletId);
        transfer.setReceiverWalletId(receiverWalletId);
        transfer.setAmount(request.getAmount());
        transfer.setFees(request.getFees() != null ? request.getFees() : BigDecimal.ZERO);
        transfer.setMotif(request.getReason());
        transfer.setStatus(TransactionStatus.EN_ATTENTE);
        transfer.setCreatedAt(LocalDateTime.now());

        Transfer saved = transferRepository.save(transfer);

        return new TransactionResponse(
                saved.getTransferId(),
                saved.getStatus().toString(),
                "Transfer initiated successfully"
        );
    }
}
