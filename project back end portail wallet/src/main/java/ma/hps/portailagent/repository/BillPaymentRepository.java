package ma.hps.portailagent.repository;

import ma.hps.portailagent.model.BillPayment;

import java.util.List;
import java.util.Optional;

public interface BillPaymentRepository {
    Optional<BillPayment> findById(Long id);
    Optional<BillPayment> findByPaymentId(String paymentId);
    List<BillPayment> findByAgentId(Long agentId);
    List<BillPayment> findByStatus(String status);
    BillPayment save(BillPayment billPayment);
    BillPayment update(BillPayment billPayment);
    void delete(Long id);
}
