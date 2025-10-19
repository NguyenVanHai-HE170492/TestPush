// JobTitleCompanyService.java
package vn.edu.fpt.HRVinaPortal.services.recruitment;

import vn.edu.fpt.HRVinaPortal.dto.recruitment.*;
import java.util.List;

public interface JobTitleCompanyService {
    List<JobTitleCompanyDto> getAll();
    JobTitleCompanyDto getById(Integer id);
    List<JobTitleCompanyDto> getByCompany(Integer companyId);
    JobTitleCompanyDto create(CreateOrUpdateJobTitleCompanyRequest req);
    JobTitleCompanyDto update(Integer id, CreateOrUpdateJobTitleCompanyRequest req);
    void delete(Integer id);
}
