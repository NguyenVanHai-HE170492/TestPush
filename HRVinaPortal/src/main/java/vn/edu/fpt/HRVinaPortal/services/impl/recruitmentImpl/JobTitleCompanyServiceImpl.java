package vn.edu.fpt.HRVinaPortal.services.impl.recruitmentImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateJobTitleCompanyRequest;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.JobTitleCompanyDto;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Company;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.JobTitleCompany;
import vn.edu.fpt.HRVinaPortal.exception.recruitment.ResourceNotFoundException;
import vn.edu.fpt.HRVinaPortal.mappers.recruitment.JobTitleCompanyMapper;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.CompanyRepository;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.JobTitleCompanyRepository;
import vn.edu.fpt.HRVinaPortal.services.recruitment.JobTitleCompanyService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobTitleCompanyServiceImpl implements JobTitleCompanyService {

    private final JobTitleCompanyRepository repo;
    private final CompanyRepository companyRepo;
    private final JobTitleCompanyMapper mapper;

    @Override
    public List<JobTitleCompanyDto> getAll() {
        return repo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public JobTitleCompanyDto getById(Integer id) {
        JobTitleCompany e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobTitle not found: " + id));
        return mapper.toDto(e);
    }

    @Override
    public List<JobTitleCompanyDto> getByCompany(Integer companyId) {
        return repo.findByCompany_CompanyId(companyId).stream()
                .map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public JobTitleCompanyDto create(CreateOrUpdateJobTitleCompanyRequest r) {
        Company company = companyRepo.findById(r.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + r.getCompanyId()));
        JobTitleCompany e = mapper.toEntityForCreate(r, company, r.getUserAction());
        e = repo.save(e);
        return mapper.toDto(e);
    }

    @Override
    public JobTitleCompanyDto update(Integer id, CreateOrUpdateJobTitleCompanyRequest r) {
        JobTitleCompany e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobTitle not found: " + id));

        Company companyOrNull = null;
        if (r.getCompanyId() != null) {
            companyOrNull = companyRepo.findById(r.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + r.getCompanyId()));
        }

        mapper.updateEntityFromRequest(r, e, companyOrNull, r.getUserAction());
        e = repo.save(e);
        return mapper.toDto(e);
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("JobTitle not found: " + id);
        repo.deleteById(id);
    }
}
