package ma.hps.portailagent.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletAction {
    @NotBlank(message = "action est requis")
    private String action;
    
    private BigDecimal amount;
    
    private String reason;
}
