// CompanyRepository.java
package vn.edu.fpt.HRVinaPortal.repositories.recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Company;
public interface CompanyRepository extends JpaRepository<Company, Integer> {}
