package vn.edu.fpt.HRVinaPortal.repositories.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.HRVinaPortal.entities.admin.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByCompanyEmail(String email);

    boolean existsByCompanyEmail(String email);
}
