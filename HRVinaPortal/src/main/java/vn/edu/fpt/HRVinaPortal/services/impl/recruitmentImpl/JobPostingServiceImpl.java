// src/main/java/vn/edu/fpt/HRVinaPortal/services/impl/recruitmentImpl/JobPostingServiceImpl.java
package vn.edu.fpt.HRVinaPortal.services.impl.recruitmentImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CompanyDto;           // ✅ NEW
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateJobPostingRequest;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.JobFiltersDto;        // ✅ NEW
import vn.edu.fpt.HRVinaPortal.dto.recruitment.JobPostingDto;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.TopJobPostingDto;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Candidate;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Company;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.JobPosting;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.JobTitleCompany;
import vn.edu.fpt.HRVinaPortal.exception.recruitment.ResourceNotFoundException;
import vn.edu.fpt.HRVinaPortal.mappers.recruitment.JobPostingMapper;
import vn.edu.fpt.HRVinaPortal.mappers.recruitment.JobTitleCompanyMapper; // ✅ NEW
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.CandidateRepository;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.CompanyRepository;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.JobPostingRepository;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.JobTitleCompanyRepository;
import vn.edu.fpt.HRVinaPortal.services.recruitment.JobPostingService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository repo;
    private final CompanyRepository companyRepo;
    private final JobTitleCompanyRepository jobTitleRepo;
    private final CandidateRepository candidateRepo;
    private final JobPostingMapper mapper;

    // ✅ NEW: cần mapper để toDtoList()
    private final JobTitleCompanyMapper titleMapper;

    @Override
    public List<JobPostingDto> getAll() {
        return repo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<TopJobPostingDto> getTopByApplicants(int limit) {
        int n = (limit <= 0) ? 5 : limit;
        return repo.findTopByApplicantCount(PageRequest.of(0, n));
    }

    @Override
    public JobPostingDto getById(Integer id) {
        JobPosting e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting not found: " + id));
        return mapper.toDto(e);
    }

    @Override
    public JobPostingDto create(CreateOrUpdateJobPostingRequest r) {
        Company company = companyRepo.findById(r.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + r.getCompanyId()));
        JobTitleCompany title = jobTitleRepo.findById(r.getJobTitleCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("JobTitle not found: " + r.getJobTitleCompanyId()));

        JobPosting e = mapper.toEntityForCreate(r, company, title, r.getUserAction());
        e = repo.save(e);
        return mapper.toDto(e);
    }

    @Override
    public JobPostingDto update(Integer id, CreateOrUpdateJobPostingRequest r) {
        JobPosting e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting not found: " + id));

        Company company = companyRepo.findById(r.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + r.getCompanyId()));
        JobTitleCompany title = jobTitleRepo.findById(r.getJobTitleCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("JobTitle not found: " + r.getJobTitleCompanyId()));

        mapper.updateEntityFromRequest(r, e, company, title, r.getUserAction());
        e = repo.save(e);
        return mapper.toDto(e);
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("JobPosting not found: " + id);
        repo.deleteById(id);
    }

    @Override
    public List<JobPostingDto> search(String kw) {
        if (kw == null || kw.isBlank()) return getAll();
        return repo.search(kw.trim()).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<JobPostingDto> getByCompany(Integer companyId) {
        return repo.findByCompany_CompanyId(companyId).stream()
                .map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<JobPostingDto> getByJobTitle(Integer jobTitleCompanyId) {
        return repo.findByJobTitleCompany_JobTitleCompanyId(jobTitleCompanyId).stream()
                .map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<JobPostingDto> getByDateRange(Date from, Date to) {
        return repo.findByDateRange(from, to).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void attachCandidate(Integer jobPostingId, Integer candidateId) {
        JobPosting jp = repo.findById(jobPostingId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting not found: " + jobPostingId));
        Candidate c = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found: " + candidateId));

        List<Candidate> list = jp.getCandidates() == null ? new ArrayList<>() : jp.getCandidates();
        if (list.stream().noneMatch(x -> x.getCandidateId().equals(candidateId))) {
            list.add(c);
        }
        jp.setCandidates(list);
        repo.save(jp);
    }

    @Override
    public void detachCandidate(Integer jobPostingId, Integer candidateId) {
        JobPosting jp = repo.findById(jobPostingId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting not found: " + jobPostingId));
        if (jp.getCandidates() != null) {
            jp.getCandidates().removeIf(c -> c.getCandidateId().equals(candidateId));
        }
        repo.save(jp);
    }

    // ===== NEW: filters =====
    @Override
    public JobFiltersDto getFilters() {
        var titles = titleMapper.toDtoList(jobTitleRepo.findAll()); // ✅ titleMapper đã có

        var companies = companyRepo.findAll().stream().map(c -> {
            var d = new CompanyDto();     // ✅ CompanyDto có @Data -> có setter
            d.setCompanyId(c.getCompanyId());
            d.setCompanyName(c.getCompanyName());
            d.setAddress(c.getAddress());
            d.setEmail(c.getEmail());
            d.setPhone(c.getPhone());
            return d;
        }).collect(Collectors.toList());

        var locations = repo.distinctLocations();  // ✅ cần method trong Repository
        var workTypes = repo.distinctWorkTypes();  // ✅ cần method trong Repository

        return new JobFiltersDto(titles, companies, locations, workTypes);
    }

    // ===== NEW: pageable advanced search (map Page) =====
    @Override
    public Page<JobPostingDto> searchAdvanced(String keyword,
                                              Integer jobTitleCompanyId,
                                              Integer companyId,
                                              String location,
                                              String workType,
                                              org.springframework.data.domain.Pageable pageable) {
        Page<JobPosting> p = repo.searchAdvanced(
                (keyword == null || keyword.isBlank()) ? null : keyword.trim(),
                jobTitleCompanyId,
                companyId,
                (location == null || location.isBlank()) ? null : location.trim(),
                (workType == null || workType.isBlank()) ? null : workType.trim(),
                pageable
        );
        return p.map(mapper::toDto);
    }
}
