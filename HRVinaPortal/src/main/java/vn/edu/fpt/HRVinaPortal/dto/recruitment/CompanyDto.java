// CompanyDto.java
package vn.edu.fpt.HRVinaPortal.dto.recruitment;
import lombok.Data;

@Data
public class CompanyDto {
    private Integer companyId;
    private String companyName;
    private String address;
    private String email;
    private String phone;
}
