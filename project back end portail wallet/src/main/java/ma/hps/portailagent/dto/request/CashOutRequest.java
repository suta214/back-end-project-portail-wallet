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
public class CashOutRequest {
    private String accountId;
    
    @NotNull(message = "amount est requis")
    private BigDecimal amount;
    
    private BigDecimal fees;
    
    @NotBlank(message = "clientPhone est requis")
    private String clientPhone;
}
