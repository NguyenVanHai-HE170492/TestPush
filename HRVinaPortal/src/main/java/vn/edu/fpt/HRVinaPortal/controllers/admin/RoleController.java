package vn.edu.fpt.HRVinaPortal.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.HRVinaPortal.dto.admin.RoleDto;
import vn.edu.fpt.HRVinaPortal.services.admin.RoleService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestBody RoleDto roleDto) {
        return new ResponseEntity<>(roleService.createRole(roleDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Integer id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable Integer id, @RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(roleService.updateRole(id, roleDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
