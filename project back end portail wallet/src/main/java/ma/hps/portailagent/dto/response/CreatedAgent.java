package ma.hps.portailagent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.hps.portailagent.enums.AgentType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedAgent {
    private Long id;
    private String identifiant;
    private String firstName;
    private String lastName;
    private AgentType agentType;
    private String email;
    private String phone;
    private String status;
    private String createdAt;
    private String otpChannel;
}
