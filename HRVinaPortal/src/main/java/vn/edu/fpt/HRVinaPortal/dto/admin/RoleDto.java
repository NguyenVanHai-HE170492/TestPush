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
public class RoleDto {
    private Integer roleId;
    private String roleName;
    private String createdBy;
    private LocalDate createdAt;
    private String updatedBy;
    private LocalDate updatedAt;
}

