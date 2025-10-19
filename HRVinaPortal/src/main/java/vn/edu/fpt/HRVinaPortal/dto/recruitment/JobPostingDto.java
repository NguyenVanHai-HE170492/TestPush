// JobPostingDto.java
package vn.edu.fpt.HRVinaPortal.dto.recruitment;
import lombok.Data;
import java.util.Date;

@Data
public class JobPostingDto {
    private Integer jobPostingId;
    private String jobName;
    private String address;
    private String salary;
    private String workType;
    private String jobDescription;
    private String jobRequirement;
    private String benefits;
    private String status;
    private Date startDate;
    private Date endDate;
    private Integer companyId;
    private String companyName;
    private Integer jobTitleCompanyId;
    private String titleName;
}
