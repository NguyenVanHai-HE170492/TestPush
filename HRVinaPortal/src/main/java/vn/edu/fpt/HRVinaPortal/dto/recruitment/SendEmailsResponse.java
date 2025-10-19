package vn.edu.fpt.HRVinaPortal.dto.recruitment;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class SendEmailsResponse {
    private int sent;
    private List<Integer> candidateIds;
}
