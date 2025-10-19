// src/main/java/vn/edu/fpt/HRVinaPortal/services/recruitment/JobPostingService.java
package vn.edu.fpt.HRVinaPortal.services.recruitment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.*;

import java.util.Date;
import java.util.List;

public interface JobPostingService {
    List<JobPostingDto> getAll();
    JobPostingDto getById(Integer id);
    JobPostingDto create(CreateOrUpdateJobPostingRequest req);
    JobPostingDto update(Integer id, CreateOrUpdateJobPostingRequest req);
    void delete(Integer id);

    List<JobPostingDto> search(String keyword);
    List<JobPostingDto> getByCompany(Integer companyId);
    List<JobPostingDto> getByJobTitle(Integer jobTitleCompanyId);
    List<JobPostingDto> getByDateRange(Date from, Date to);

    void attachCandidate(Integer jobPostingId, Integer candidateId);
    void detachCandidate(Integer jobPostingId, Integer candidateId);
    List<TopJobPostingDto> getTopByApplicants(int limit);

    // ==== NEW ====
    JobFiltersDto getFilters();
    Page<JobPostingDto> searchAdvanced(String keyword,
                                       Integer jobTitleCompanyId,
                                       Integer companyId,
                                       String location,
                                       String workType,
                                       Pageable pageable);
}
