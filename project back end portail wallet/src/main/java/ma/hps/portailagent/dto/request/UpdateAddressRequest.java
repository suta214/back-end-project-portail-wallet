package ma.hps.portailagent.dto.request;

import lombok.Data;

@Data
public class UpdateAddressRequest {
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String country;
    private String region;
    private String city;
    private String postalCode;
}
