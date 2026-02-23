package ma.hps.portailagent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String cin;
    private String phone;
    private String email;
    private String walletId;
    private BigDecimal balance;
    private String status;
    private boolean kycVerified;
}
