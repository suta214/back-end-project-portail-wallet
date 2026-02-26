package ma.hps.portailagent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.hps.portailagent.enums.AgentType;
import ma.hps.portailagent.enums.Privilege;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agent {
    private Long id;
    private String agentCode;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private AgentType agentType;
    private boolean enabled;
    private LocalDateTime createdAt;
    private List<Privilege> privileges;
    private boolean mustChangePassword;
    private String otpChannel;
}
