package ma.hps.portailagent.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientEnrollRequest {
    @NotBlank(message = "lastName est requis")
    private String lastName;
    
    @NotBlank(message = "firstName est requis")
    private String firstName;
    
    @NotBlank(message = "cin est requis")
    private String cin;
    
    @NotBlank(message = "phone est requis")
    private String phone;
    
    @Email(message = "email doit être valide")
    private String email;
    
    private String nationality;
    private String birthDate;
    private String birthPlace;
    private String gender;
    private String address;
    private String city;
    private String idType;
    private String idNumber;
    private String idIssueDate;
    private String idExpiryDate;
}
