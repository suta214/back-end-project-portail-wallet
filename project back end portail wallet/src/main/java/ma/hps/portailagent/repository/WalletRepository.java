package ma.hps.portailagent.repository;

import ma.hps.portailagent.model.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepository {
    Optional<Wallet> findById(Long id);
    Optional<Wallet> findByWalletId(String walletId);
    Optional<Wallet> findByPhone(String phone);
    List<Wallet> findAll();
    List<Wallet> findByStatus(String status);
    List<Wallet> findByType(String type);
    List<Wallet> search(String searchTerm);
    Wallet save(Wallet wallet);
    Wallet update(Wallet wallet);
    void delete(Long id);
}
