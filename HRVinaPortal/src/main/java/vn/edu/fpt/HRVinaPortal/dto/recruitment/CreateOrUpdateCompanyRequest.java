// CreateOrUpdateCompanyRequest.java
package vn.edu.fpt.HRVinaPortal.dto.recruitment;
import lombok.Data;
@Data
public class CreateOrUpdateCompanyRequest {
    private String companyName;
    private String address;
    private String email;
    private String phone;
    private String userAction;
}
