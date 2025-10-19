// CompanyService.java
package vn.edu.fpt.HRVinaPortal.services.recruitment;

import vn.edu.fpt.HRVinaPortal.dto.recruitment.*;
import java.util.List;

public interface CompanyService {
    List<CompanyDto> getAll();
    CompanyDto getById(Integer id);
    CompanyDto create(CreateOrUpdateCompanyRequest req);
    CompanyDto update(Integer id, CreateOrUpdateCompanyRequest req);
    void delete(Integer id);
}
