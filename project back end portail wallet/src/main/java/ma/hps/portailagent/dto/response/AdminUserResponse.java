package ma.hps.portailagent.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import ma.hps.portailagent.enums.AgentType;
import ma.hps.portailagent.enums.Privilege;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class AdminUserResponse {
    private Long id;
    private String identifiant;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String idType;
    private String idNumber;
    private BigDecimal commission;
    private String rib;
    private String status;
    private AgentType agentType;
    private List<Privilege> privileges;
    // Contrat
    private String contractType;
    private LocalDate contractDate;
    private String signatory;
    private String patentNumber;
    // Adresse
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String country;
    private String region;
    private String city;
    private String postalCode;
    // Méta
    private String createdAt;
}
