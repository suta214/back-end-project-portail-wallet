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
public class BillPaymentRequest {
    @NotBlank(message = "billerId est requis")
    private String billerId;
    
    @NotBlank(message = "customerRef est requis")
    private String customerRef;
    
    private String contractNumber;
    
    @NotNull(message = "amount est requis")
    private BigDecimal amount;
    
    private BigDecimal fees;
}
