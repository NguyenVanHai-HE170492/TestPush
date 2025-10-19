package vn.edu.fpt.HRVinaPortal.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {

    private Integer accountId;

    private String companyEmail;

    private String password; // ⚠️ Có thể ẩn trong response nếu cần bảo mật

    private String createdBy;

    private LocalDate createdAt;

    private String updatedBy;

    private LocalDate updatedAt;

    private Integer employeeId;

    private RoleDto role; // Liên kết RoleDto
}
