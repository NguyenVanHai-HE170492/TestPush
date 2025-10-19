package vn.edu.fpt.HRVinaPortal.services.impl.recruitmentImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CompanyDto;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateCompanyRequest;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Company;
import vn.edu.fpt.HRVinaPortal.exception.recruitment.ResourceNotFoundException;
import vn.edu.fpt.HRVinaPortal.mappers.recruitment.CompanyMapper;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.CompanyRepository;
import vn.edu.fpt.HRVinaPortal.services.recruitment.CompanyService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository repo;
    private final CompanyMapper mapper;

    @Override
    public List<CompanyDto> getAll() {
        return repo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CompanyDto getById(Integer id) {
        Company c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + id));
        return mapper.toDto(c);
    }

    @Override
    public CompanyDto create(CreateOrUpdateCompanyRequest r) {
        Company e = mapper.toEntityForCreate(r, r.getUserAction());
        e = repo.save(e);
        return mapper.toDto(e);
    }

    @Override
    public CompanyDto update(Integer id, CreateOrUpdateCompanyRequest r) {
        Company e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + id));
        mapper.updateEntityFromRequest(r, e, r.getUserAction());
        e = repo.save(e);
        return mapper.toDto(e);
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("Company not found: " + id);
        repo.deleteById(id);
    }
}
