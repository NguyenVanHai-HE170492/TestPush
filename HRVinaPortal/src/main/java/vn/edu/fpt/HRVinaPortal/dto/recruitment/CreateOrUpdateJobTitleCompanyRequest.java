// CreateOrUpdateJobTitleCompanyRequest.java
package vn.edu.fpt.HRVinaPortal.dto.recruitment;
import lombok.Data;
@Data
public class CreateOrUpdateJobTitleCompanyRequest {
    private String titleName;
    private Integer companyId;   // bắt buộc
    private String userAction;
}
