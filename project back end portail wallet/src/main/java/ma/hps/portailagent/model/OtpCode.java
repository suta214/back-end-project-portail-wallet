package ma.hps.portailagent.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OtpCode {
    private Long id;
    private Long agentId;
    private String code;
    private LocalDateTime expiredAt;
    private boolean used;
    private LocalDateTime createdAt;
}
