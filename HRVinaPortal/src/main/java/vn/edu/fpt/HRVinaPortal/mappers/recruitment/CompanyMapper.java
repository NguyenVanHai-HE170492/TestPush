package vn.edu.fpt.HRVinaPortal.mappers.recruitment;

import org.springframework.stereotype.Component;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CompanyDto;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateCompanyRequest;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Company;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompanyMapper {

    // Entity -> DTO
    public CompanyDto toDto(Company e){
        if(e==null) return null;
        CompanyDto d = new CompanyDto();
        d.setCompanyId(e.getCompanyId());
        d.setCompanyName(e.getCompanyName());
        d.setAddress(e.getAddress());
        d.setEmail(e.getEmail());
        d.setPhone(e.getPhone());
        return d;
    }
    public List<CompanyDto> toDtoList(List<Company> list){
        return list==null ? List.of() : list.stream().map(this::toDto).collect(Collectors.toList());
    }

    // Request -> Entity (CREATE)
    public Company toEntityForCreate(CreateOrUpdateCompanyRequest r, String actor){
        Company e = new Company();
        mapCommon(r, e);
        e.setCreatedBy(actor);
        e.setCreatedAt(new Date());
        return e;
    }

    // PATCH/UPDATE
    public void updateEntityFromRequest(CreateOrUpdateCompanyRequest r, Company e, String actor){
        mapCommon(r, e);
        e.setUpdatedBy(actor);
        e.setUpdatedAt(new Date());
    }

    private void mapCommon(CreateOrUpdateCompanyRequest r, Company e){
        if(r.getCompanyName()!=null) e.setCompanyName(r.getCompanyName());
        if(r.getAddress()!=null)     e.setAddress(r.getAddress());
        if(r.getEmail()!=null)       e.setEmail(r.getEmail());
        if(r.getPhone()!=null)       e.setPhone(r.getPhone());
    }
}
