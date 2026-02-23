package ma.hps.portailagent.repository.impl;

import ma.hps.portailagent.model.BillPayment;
import ma.hps.portailagent.repository.BillPaymentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class BillPaymentRepositoryImpl implements BillPaymentRepository {
    private final JdbcTemplate jdbcTemplate;

    public BillPaymentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<BillPayment> billPaymentRowMapper = (rs, rowNum) -> {
        BillPayment billPayment = new BillPayment();
        billPayment.setId(rs.getLong("id"));
        billPayment.setPaymentId(rs.getString("payment_id"));
        billPayment.setAgentId(rs.getLong("agent_id"));
        billPayment.setBillerId(rs.getString("biller_id"));
        billPayment.setCustomerRef(rs.getString("customer_ref"));
        billPayment.setContractNumber(rs.getString("contract_number"));
        billPayment.setAmount(rs.getBigDecimal("amount"));
        billPayment.setFees(rs.getBigDecimal("fees"));
        billPayment.setStatus(ma.hps.portailagent.enums.TransactionStatus.valueOf(rs.getString("status")));
        billPayment.setInvoiceRef(rs.getString("invoice_ref"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            billPayment.setCreatedAt(createdAt.toLocalDateTime());
        }
        return billPayment;
    };

    @Override
    public Optional<BillPayment> findById(Long id) {
        String sql = "SELECT * FROM bill_payments WHERE id = ?";
        try {
            BillPayment billPayment = jdbcTemplate.queryForObject(sql, billPaymentRowMapper, id);
            return Optional.of(billPayment);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<BillPayment> findByPaymentId(String paymentId) {
        String sql = "SELECT * FROM bill_payments WHERE payment_id = ?";
        try {
            BillPayment billPayment = jdbcTemplate.queryForObject(sql, billPaymentRowMapper, paymentId);
            return Optional.of(billPayment);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<BillPayment> findByAgentId(Long agentId) {
        String sql = "SELECT * FROM bill_payments WHERE agent_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, billPaymentRowMapper, agentId);
    }

    @Override
    public List<BillPayment> findByStatus(String status) {
        String sql = "SELECT * FROM bill_payments WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, billPaymentRowMapper, status);
    }

    @Override
    public BillPayment save(BillPayment billPayment) {
        String sql = "INSERT INTO bill_payments (payment_id, agent_id, biller_id, customer_ref, contract_number, amount, fees, status, invoice_ref, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
            billPayment.getPaymentId(),
            billPayment.getAgentId(),
            billPayment.getBillerId(),
            billPayment.getCustomerRef(),
            billPayment.getContractNumber(),
            billPayment.getAmount(),
            billPayment.getFees(),
            billPayment.getStatus().toString(),
            billPayment.getInvoiceRef(),
            Timestamp.valueOf(LocalDateTime.now())
        );

        Long id = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
        billPayment.setId(id);
        billPayment.setCreatedAt(LocalDateTime.now());
        
        return billPayment;
    }

    @Override
    public BillPayment update(BillPayment billPayment) {
        String sql = "UPDATE bill_payments SET biller_id = ?, customer_ref = ?, contract_number = ?, amount = ?, fees = ?, status = ?, invoice_ref = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
            billPayment.getBillerId(),
            billPayment.getCustomerRef(),
            billPayment.getContractNumber(),
            billPayment.getAmount(),
            billPayment.getFees(),
            billPayment.getStatus().toString(),
            billPayment.getInvoiceRef(),
            billPayment.getId()
        );
        
        return billPayment;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM bill_payments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
