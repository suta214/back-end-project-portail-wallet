package ma.hps.portailagent.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "agentId est requis")
    private String agentId;
    
    @NotBlank(message = "password est requis")
    private String password;
}
