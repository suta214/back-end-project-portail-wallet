package ma.hps.portailagent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private Long id;
    private Long agentId;
    private String action;
    private String entity;
    private String entityId;
    private String details;
    private String ipAddress;
    private LocalDateTime createdAt;
}
