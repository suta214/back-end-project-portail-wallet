package ma.hps.portailagent.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String idType;
    private String idNumber;
    private BigDecimal commission;
}
