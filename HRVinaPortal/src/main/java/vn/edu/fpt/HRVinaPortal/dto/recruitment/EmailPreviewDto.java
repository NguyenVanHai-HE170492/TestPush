package vn.edu.fpt.HRVinaPortal.dto.recruitment;

import lombok.Data;

@Data
public class EmailPreviewDto {
    private Integer candidateId;
    private String candidateName;
    private String toEmail;
    private String subject;
    private String htmlBody;
}
