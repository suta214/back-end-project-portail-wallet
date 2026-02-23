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
public class CreateWalletRequest {
    @NotBlank(message = "ownerName est requis")
    private String ownerName;
    
    @NotBlank(message = "phone est requis")
    private String phone;
    
    @NotBlank(message = "email est requis")
    private String email;
    
    @NotBlank(message = "type est requis")
    private String type;
    
    @NotNull(message = "dailyLimit est requis")
    private BigDecimal dailyLimit;
    
    private BigDecimal monthlyLimit;
    
    @NotBlank(message = "currency est requis")
    private String currency;
    
    private boolean kycVerified;
}
