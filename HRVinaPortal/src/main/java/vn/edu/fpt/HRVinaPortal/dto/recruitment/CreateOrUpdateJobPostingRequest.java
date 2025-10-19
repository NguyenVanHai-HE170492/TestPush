// CreateOrUpdateJobPostingRequest.java
package vn.edu.fpt.HRVinaPortal.dto.recruitment;
import lombok.Data;
import java.util.Date;

@Data
public class CreateOrUpdateJobPostingRequest {
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
    private Integer jobTitleCompanyId;
    private String userAction;
}
