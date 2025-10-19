package vn.edu.fpt.HRVinaPortal.mappers.recruitment;

import org.springframework.stereotype.Component;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateJobTitleCompanyRequest;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.JobTitleCompanyDto;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Company;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.JobTitleCompany;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobTitleCompanyMapper {

    public JobTitleCompanyDto toDto(JobTitleCompany e){
        if(e==null) return null;
        JobTitleCompanyDto d = new JobTitleCompanyDto();
        d.setJobTitleCompanyId(e.getJobTitleCompanyId());
        d.setTitleName(e.getTitleName());
        if(e.getCompany()!=null){
            d.setCompanyId(e.getCompany().getCompanyId());
            d.setCompanyName(e.getCompany().getCompanyName());
        }
        return d;
    }
    public List<JobTitleCompanyDto> toDtoList(List<JobTitleCompany> list){
        return list==null ? List.of() : list.stream().map(this::toDto).collect(Collectors.toList());
    }

    // CREATE (Company đã nạp sẵn từ service)
    public JobTitleCompany toEntityForCreate(CreateOrUpdateJobTitleCompanyRequest r,
                                             Company company,
                                             String actor){
        JobTitleCompany e = new JobTitleCompany();
        mapCommon(r, e);
        e.setCompany(company);
        e.setCreatedBy(actor);
        e.setCreatedAt(new Date());
        return e;
    }

    // UPDATE (có thể đổi company)
    public void updateEntityFromRequest(CreateOrUpdateJobTitleCompanyRequest r,
                                        JobTitleCompany e,
                                        Company newCompanyOrNull,
                                        String actor){
        mapCommon(r, e);
        if(newCompanyOrNull!=null) e.setCompany(newCompanyOrNull);
        e.setUpdatedBy(actor);
        e.setUpdatedAt(new Date());
    }

    private void mapCommon(CreateOrUpdateJobTitleCompanyRequest r, JobTitleCompany e){
        if(r.getTitleName()!=null) e.setTitleName(r.getTitleName());
    }
}
