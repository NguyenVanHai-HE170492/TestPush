// src/main/java/vn/edu/fpt/HRVinaPortal/dto/recruitment/TopJobPostingDto.java
package vn.edu.fpt.HRVinaPortal.dto.recruitment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class TopJobPostingDto {
    private Integer jobPostingId;
    private String jobName;
    private String companyName;
    private String titleName;        // tên chức danh (JobTitleCompany)
    private Long applicantCount;     // số ứng viên ứng tuyển
}
