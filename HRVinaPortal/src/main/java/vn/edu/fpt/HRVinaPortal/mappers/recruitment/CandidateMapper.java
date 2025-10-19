package vn.edu.fpt.HRVinaPortal.mappers.recruitment;

import org.springframework.stereotype.Component;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CandidateDto;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateCandidateRequest;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Candidate;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CandidateMapper {

    // Entity -> DTO
    public CandidateDto toDto(Candidate e) {
        if (e == null) return null;
        CandidateDto d = new CandidateDto();
        d.setCandidateId(e.getCandidateId());
        d.setFullName(e.getFullName());
        d.setGender(e.getGender());
        d.setDob(e.getBirthDate());
        d.setPhone(e.getPhone());
        d.setEmail(e.getEmail());
        d.setAddress(e.getAddress());
        d.setResumeUrl(e.getCvLink());
        d.setStatus(e.getStatus());

        // ✅ THÊM MỚI: Mapping danh sách công việc đã ứng tuyển
        if (e.getAppliedJobs() != null) {
            List<CandidateDto.AppliedJobInfo> appliedJobInfos = e.getAppliedJobs().stream()
                    .map(jobPosting -> {
                        CandidateDto.AppliedJobInfo info = new CandidateDto.AppliedJobInfo();
                        info.setJobPostingId(jobPosting.getJobPostingId());
                        info.setJobName(jobPosting.getJobName());
                        // Lấy thông tin từ các quan hệ đã có
                        if (jobPosting.getCompany() != null) {
                            info.setCompanyName(jobPosting.getCompany().getCompanyName());
                        }
                        if (jobPosting.getJobTitleCompany() != null) {
                            info.setJobTitleName(jobPosting.getJobTitleCompany().getTitleName());
                        }
                        return info;
                    }).collect(Collectors.toList());
            d.setAppliedJobs(appliedJobInfos);
        }

        return d;
    }

    public List<CandidateDto> toDtoList(List<Candidate> list) {
        return list == null ? Collections.emptyList() : list.stream().map(this::toDto).collect(Collectors.toList());
    }

    // CREATE (DTO cũ)
    public Candidate toEntityForCreate(CreateOrUpdateCandidateRequest r, String actor) {
        Candidate e = new Candidate();
        mapCommon(r, e);
        e.setCreatedBy(actor);
        e.setCreatedAt(new Date());
        return e;
    }

    // UPDATE (DTO cũ)
    public void updateEntityFromRequest(CreateOrUpdateCandidateRequest r, Candidate e, String actor) {
        mapCommon(r, e);
        e.setUpdatedBy(actor);
        e.setUpdatedAt(new Date());
    }

    private void mapCommon(CreateOrUpdateCandidateRequest r, Candidate e) {
        if (r.getFullName() != null) e.setFullName(r.getFullName());
        if (r.getGender() != null) e.setGender(r.getGender());
        // DTO.dob -> Entity.birthDate
        if (r.getDob() != null) e.setBirthDate(r.getDob());
        if (r.getPhone() != null) e.setPhone(r.getPhone());
        if (r.getEmail() != null) e.setEmail(r.getEmail());
        if (r.getAddress() != null) e.setAddress(r.getAddress());
        // DTO.resumeUrl -> Entity.cvLink
        if (r.getResumeUrl() != null) e.setCvLink(r.getResumeUrl());
        if (r.getStatus() != null) e.setStatus(r.getStatus());
    }
}
