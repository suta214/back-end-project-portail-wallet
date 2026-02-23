package ma.hps.portailagent.service;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.CreateWalletRequest;
import ma.hps.portailagent.dto.request.WalletAction;
import ma.hps.portailagent.dto.response.BeneficiaryVerification;
import ma.hps.portailagent.dto.response.WalletResponse;
import ma.hps.portailagent.enums.WalletStatus;
import ma.hps.portailagent.exception.ResourceNotFoundException;
import ma.hps.portailagent.model.Wallet;
import ma.hps.portailagent.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    public List<WalletResponse> getWallets(String status, String type, String search) {
        List<Wallet> wallets;
        
        if (search != null && !search.isEmpty()) {
            wallets = walletRepository.search(search);
        } else if (status != null && !status.isEmpty()) {
            wallets = walletRepository.findByStatus(status);
        } else if (type != null && !type.isEmpty()) {
            wallets = walletRepository.findByType(type);
        } else {
            wallets = walletRepository.findAll();
        }
        
        return wallets.stream().map(this::toWalletResponse).collect(Collectors.toList());
    }

    public WalletResponse getWalletById(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        return toWalletResponse(wallet);
    }

    public BeneficiaryVerification verifyWallet(String phone) {
        Wallet wallet = walletRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for phone: " + phone));
        
        return new BeneficiaryVerification(
                wallet.getOwnerName(),
                wallet.getPhone(),
                wallet.getType(),
                wallet.getWalletId()
        );
    }

    public WalletResponse createWallet(CreateWalletRequest request) {
        Wallet wallet = new Wallet();
        wallet.setWalletId("WALLET-" + UUID.randomUUID().toString().substring(0, 12));
        wallet.setOwnerName(request.getOwnerName());
        wallet.setPhone(request.getPhone());
        wallet.setType(request.getType());
        wallet.setStatus(WalletStatus.ACTIF);
        wallet.setBalance(request.getDailyLimit());
        wallet.setDailyLimit(request.getDailyLimit());
        wallet.setCurrency(request.getCurrency());
        wallet.setCreatedAt(LocalDateTime.now());

        Wallet saved = walletRepository.save(wallet);
        return toWalletResponse(saved);
    }

    public void applyAction(Long walletId, WalletAction action) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        switch (action.getAction()) {
            case "suspend":
                wallet.setStatus(WalletStatus.SUSPENDU);
                break;
            case "reactivate":
                wallet.setStatus(WalletStatus.ACTIF);
                break;
            case "close":
                wallet.setStatus(WalletStatus.FERME);
                break;
        }

        walletRepository.update(wallet);
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setWalletId(wallet.getWalletId());
        response.setOwnerName(wallet.getOwnerName());
        response.setPhone(wallet.getPhone());
        response.setType(wallet.getType());
        response.setStatus(wallet.getStatus().toString());
        response.setBalance(wallet.getBalance());
        response.setDailyLimit(wallet.getDailyLimit());
        response.setCurrency(wallet.getCurrency());
        return response;
    }
}
