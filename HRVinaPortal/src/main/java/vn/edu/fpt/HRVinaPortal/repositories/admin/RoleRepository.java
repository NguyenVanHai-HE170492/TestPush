package vn.edu.fpt.HRVinaPortal.repositories.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.HRVinaPortal.entities.admin.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    boolean existsByRoleName(String roleName);
}

