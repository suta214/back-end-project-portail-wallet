package ma.hps.portailagent.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
    @NotBlank(message = "currentPassword est requis")
    private String currentPassword;
    
    @NotBlank(message = "newPassword est requis")
    private String newPassword;
}
