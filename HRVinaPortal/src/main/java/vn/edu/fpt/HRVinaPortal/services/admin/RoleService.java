package vn.edu.fpt.HRVinaPortal.services.admin;

import vn.edu.fpt.HRVinaPortal.dto.admin.RoleDto;

import java.util.List;

public interface RoleService {

    RoleDto createRole(RoleDto roleDto);

    List<RoleDto> getAllRoles();

    RoleDto getRoleById(Integer roleId);

    RoleDto updateRole(Integer roleId, RoleDto roleDto);

    void deleteRole(Integer roleId);
}
