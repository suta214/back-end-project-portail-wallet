package ma.hps.portailagent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private Long id;
    private String lastName;
    private String firstName;
    private String cin;
    private String phone;
    private String email;
    private String walletId;
    private String status;
    private boolean kycVerified;
    private LocalDateTime createdAt;
}
