package ma.hps.portailagent.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSettingsRequest {
    private String language;
    private String timezone;
    private Boolean notificationsEnabled;
    private Boolean twoFactorEnabled;
}
