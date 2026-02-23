package ma.hps.portailagent.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    @NotBlank(message = "transferType est requis")
    private String transferType;
    
    @NotBlank(message = "senderPhone est requis")
    private String senderPhone;
    
    @NotBlank(message = "beneficiaryPhone est requis")
    private String beneficiaryPhone;
    
    @NotNull(message = "amount est requis")
    private BigDecimal amount;
    
    private BigDecimal fees;
    
    private String reason;
}
