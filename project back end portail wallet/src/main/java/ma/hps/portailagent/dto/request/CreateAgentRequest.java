package ma.hps.portailagent.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.hps.portailagent.enums.AgentType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgentRequest {
    @NotBlank(message = "identifiant est requis")
    private String identifiant;
    
    private String commission;
    
    @NotBlank(message = "firstName est requis")
    private String firstName;
    
    @NotBlank(message = "lastName est requis")
    private String lastName;
    
    @NotBlank(message = "agentType est requis")
    private AgentType agentType;
    
    @NotBlank(message = "idType est requis")
    private String idType;
    
    @NotBlank(message = "idNumber est requis")
    private String idNumber;
    
    @Email(message = "email doit être valide")
    private String email;
    private String emailConfirmation;
    
    @NotBlank(message = "phone est requis")
    private String phone;
    
    private String contractType;
    private String patentNumber;
    private String contractDate;
    private String signatory;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String country;
    private String region;
    private String city;
    private String postalCode;
}
