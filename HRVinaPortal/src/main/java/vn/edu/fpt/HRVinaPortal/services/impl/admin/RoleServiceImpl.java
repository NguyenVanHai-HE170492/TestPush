package vn.edu.fpt.HRVinaPortal.services.impl.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.HRVinaPortal.dto.admin.RoleDto;
import vn.edu.fpt.HRVinaPortal.entities.admin.Role;
import vn.edu.fpt.HRVinaPortal.mappers.admin.RoleMapper;
import vn.edu.fpt.HRVinaPortal.repositories.admin.RoleRepository;
import vn.edu.fpt.HRVinaPortal.services.admin.RoleService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        Role role = RoleMapper.toEntity(roleDto);
        role.setCreatedAt(LocalDate.now());
        roleRepository.save(role);
        return RoleMapper.toDto(role);
    }

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(RoleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDto getRoleById(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return RoleMapper.toDto(role);
    }

    @Override
    public RoleDto updateRole(Integer roleId, RoleDto roleDto) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setRoleName(roleDto.getRoleName());
        role.setUpdatedAt(LocalDate.now());
        roleRepository.save(role);

        return RoleMapper.toDto(role);
    }

    @Override
    public void deleteRole(Integer roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new RuntimeException("Role not found");
        }
        roleRepository.deleteById(roleId);
    }
}
