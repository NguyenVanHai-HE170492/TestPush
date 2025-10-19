// JobTitleRepository.java
package vn.edu.fpt.HRVinaPortal.repositories.recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.JobTitleCompany;
import java.util.List;

public interface JobTitleCompanyRepository extends JpaRepository<JobTitleCompany, Integer> {
    List<JobTitleCompany> findByCompany_CompanyId(Integer companyId);
}
