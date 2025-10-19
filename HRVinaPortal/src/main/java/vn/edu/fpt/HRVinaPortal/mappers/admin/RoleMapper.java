package vn.edu.fpt.HRVinaPortal.mappers.admin;

import vn.edu.fpt.HRVinaPortal.entities.admin.Role;
import vn.edu.fpt.HRVinaPortal.dto.admin.RoleDto;

public class RoleMapper {

    public static RoleDto toDto(Role role) {
        if (role == null) return null;

        return RoleDto.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .createdBy(role.getCreatedBy())
                .createdAt(role.getCreatedAt())
                .updatedBy(role.getUpdatedBy())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    public static Role toEntity(RoleDto dto) {
        if (dto == null) return null;

        Role role = new Role();
        role.setRoleId(dto.getRoleId());
        role.setRoleName(dto.getRoleName());
        role.setCreatedBy(dto.getCreatedBy());
        role.setCreatedAt(dto.getCreatedAt());
        role.setUpdatedBy(dto.getUpdatedBy());
        role.setUpdatedAt(dto.getUpdatedAt());
        return role;
    }
}
