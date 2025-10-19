package vn.edu.fpt.HRVinaPortal.mappers.recruitment;

import org.springframework.stereotype.Component;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateJobPostingRequest;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.JobPostingDto;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Company;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.JobPosting;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.JobTitleCompany;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobPostingMapper {

    public JobPostingDto toDto(JobPosting e){
        if(e==null) return null;
        JobPostingDto d = new JobPostingDto();
        d.setJobPostingId(e.getJobPostingId());
        d.setJobName(e.getJobName());
        d.setAddress(e.getAddress());
        d.setSalary(e.getSalary());
        d.setWorkType(e.getWorkType());
        d.setJobDescription(e.getJobDescription());
        d.setJobRequirement(e.getJobRequirement());
        d.setBenefits(e.getBenefits());
        d.setStatus(e.getStatus());
        d.setStartDate(e.getStartDate());
        d.setEndDate(e.getEndDate());
        if(e.getCompany()!=null){
            d.setCompanyId(e.getCompany().getCompanyId());
            d.setCompanyName(e.getCompany().getCompanyName());
        }
        if(e.getJobTitleCompany()!=null){
            d.setJobTitleCompanyId(e.getJobTitleCompany().getJobTitleCompanyId());
            d.setTitleName(e.getJobTitleCompany().getTitleName());
        }
        return d;
    }
    public List<JobPostingDto> toDtoList(List<JobPosting> list){
        return list==null ? List.of() : list.stream().map(this::toDto).collect(Collectors.toList());
    }

    // CREATE
    public JobPosting toEntityForCreate(CreateOrUpdateJobPostingRequest r,
                                        Company company,
                                        JobTitleCompany jobTitle,
                                        String actor){
        JobPosting e = new JobPosting();
        mapCommon(r, e);
        e.setCompany(company);
        e.setJobTitleCompany(jobTitle);
        e.setCreatedBy(actor);
        e.setCreatedAt(new Date());
        return e;
    }

    // UPDATE
    public void updateEntityFromRequest(CreateOrUpdateJobPostingRequest r,
                                        JobPosting e,
                                        Company company,
                                        JobTitleCompany jobTitle,
                                        String actor){
        mapCommon(r, e);
        if(company!=null)  e.setCompany(company);
        if(jobTitle!=null) e.setJobTitleCompany(jobTitle);
        e.setUpdatedBy(actor);
        e.setUpdatedAt(new Date());
    }

    private void mapCommon(CreateOrUpdateJobPostingRequest r, JobPosting e){
        if(r.getJobName()!=null)        e.setJobName(r.getJobName());
        if(r.getAddress()!=null)        e.setAddress(r.getAddress());
        if(r.getSalary()!=null)         e.setSalary(r.getSalary());
        if(r.getWorkType()!=null)       e.setWorkType(r.getWorkType());
        if(r.getJobDescription()!=null) e.setJobDescription(r.getJobDescription());
        if(r.getJobRequirement()!=null) e.setJobRequirement(r.getJobRequirement());
        if(r.getBenefits()!=null)       e.setBenefits(r.getBenefits());
        if(r.getStatus()!=null)         e.setStatus(r.getStatus());
        if(r.getStartDate()!=null)      e.setStartDate(r.getStartDate());
        if(r.getEndDate()!=null)        e.setEndDate(r.getEndDate());
    }
}
