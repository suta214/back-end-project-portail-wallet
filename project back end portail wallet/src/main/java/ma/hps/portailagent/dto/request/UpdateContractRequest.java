package ma.hps.portailagent.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateContractRequest {
    private String contractType;
    private LocalDate contractDate;
    private String signatory;
    private String patentNumber;
}
