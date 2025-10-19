// src/main/java/vn/edu/fpt/HRVinaPortal/services/impl/recruitmentImpl/CandidateServiceImpl.java
package vn.edu.fpt.HRVinaPortal.services.impl.recruitmentImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CandidateDto;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateCandidateRequest;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Candidate;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.JobPosting;
import vn.edu.fpt.HRVinaPortal.exception.recruitment.ResourceNotFoundException;
import vn.edu.fpt.HRVinaPortal.mappers.recruitment.CandidateMapper;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.CandidateRepository;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.JobPostingRepository;
import vn.edu.fpt.HRVinaPortal.services.FileStorageService;
import vn.edu.fpt.HRVinaPortal.services.recruitment.CandidateService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository repo;
    private final JobPostingRepository jobRepo;
    private final CandidateMapper mapper;
    private final FileStorageService storage;

    @Override
    public List<CandidateDto> getAll() {
        return repo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CandidateDto getById(Integer id) {
        Candidate e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found: " + id));
        return mapper.toDto(e);
    }

    @Override
    @Transactional
    public CandidateDto create(CreateOrUpdateCandidateRequest r) {
        Candidate e = mapper.toEntityForCreate(r, r.getUserAction());

        // Gắn các JobPosting đã ứng tuyển (OWNING SIDE là Candidate)
        if (r.getAppliedJobPostingIds() != null && !r.getAppliedJobPostingIds().isEmpty()) {
            List<JobPosting> jobs = jobRepo.findAllById(r.getAppliedJobPostingIds());
            if (jobs.size() != r.getAppliedJobPostingIds().size()) {
                Set<Integer> found = jobs.stream().map(JobPosting::getJobPostingId).collect(Collectors.toSet());
                List<Integer> missing = r.getAppliedJobPostingIds().stream()
                        .filter(id -> !found.contains(id))
                        .collect(Collectors.toList());
                throw new ResourceNotFoundException("JobPosting not found: " + missing);
            }
            e.setAppliedJobs(jobs);
        } else {
            e.setAppliedJobs(new ArrayList<>());
        }

        e = repo.save(e);
        return mapper.toDto(e);
    }

    @Override
    @Transactional
    public CandidateDto update(Integer id, CreateOrUpdateCandidateRequest r) {
        Candidate e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found: " + id));

        mapper.updateEntityFromRequest(r, e, r.getUserAction());

        // Cho phép cập nhật lại danh sách job đã ứng tuyển
        if (r.getAppliedJobPostingIds() != null) {
            if (r.getAppliedJobPostingIds().isEmpty()) {
                e.setAppliedJobs(new ArrayList<>()); // xóa hết liên kết
            } else {
                List<JobPosting> jobs = jobRepo.findAllById(r.getAppliedJobPostingIds());
                if (jobs.size() != r.getAppliedJobPostingIds().size()) {
                    Set<Integer> found = jobs.stream().map(JobPosting::getJobPostingId).collect(Collectors.toSet());
                    List<Integer> missing = r.getAppliedJobPostingIds().stream()
                            .filter(x -> !found.contains(x))
                            .collect(Collectors.toList());
                    throw new ResourceNotFoundException("JobPosting not found: " + missing);
                }
                e.setAppliedJobs(jobs); // thay mới toàn bộ set
            }
        }

        e = repo.save(e);
        return mapper.toDto(e);
    }

    // ❌ BỎ phiên bản delete(Integer) cũ dùng existsById + deleteById
    // ✅ GIỮ MỘT PHIÊN BẢN delete có dọn Cloudinary
    @Override
    @Transactional
    public void delete(Integer id) {
        Candidate cand = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found: " + id));

        // dọn Cloudinary nếu có
        try {
            if (cand.getCvPublicId() != null && !cand.getCvPublicId().isBlank()) {
                storage.delete(cand.getCvPublicId());
            }
        } catch (Exception ex) {
            // log cảnh báo, không chặn xoá DB
            log.warn("Failed to delete CV on Cloudinary for candidate {} (publicId={}): {}",
                    id, cand.getCvPublicId(), ex.getMessage());
        }

        repo.delete(cand);
    }

    @Override
    public List<CandidateDto> search(String kw) {
        if (kw == null || kw.isBlank()) return getAll();
        return repo.search(kw.trim()).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void applyJob(Integer candidateId, Integer jobPostingId) {
        JobPosting job = jobRepo.findById(jobPostingId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting not found: " + jobPostingId));
        Candidate c = repo.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found: " + candidateId));

        if (c.getAppliedJobs() == null) c.setAppliedJobs(new ArrayList<>());
        boolean already = c.getAppliedJobs().stream()
                .anyMatch(j -> j.getJobPostingId().equals(jobPostingId));
        if (!already) {
            c.getAppliedJobs().add(job);
            repo.save(c);
        }
    }

    @Override
    public List<CandidateDto> getByJobPosting(Integer jobPostingId) {
        jobRepo.findById(jobPostingId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting not found: " + jobPostingId));
        return repo.findByJobPostingIdNative(jobPostingId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CandidateDto uploadCv(Integer candidateId, MultipartFile file) {
        Candidate cand = repo.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found: " + candidateId));

        try {
            if (cand.getCvPublicId() != null && !cand.getCvPublicId().isBlank()) {
                storage.delete(cand.getCvPublicId());
            }

            String folder = "hrvina/candidates/" + candidateId;
            Map<String, Object> res = storage.upload(file, folder);

            // ✅ getOrDefault OK vì V=Object
            Object urlObj = res.getOrDefault("secure_url", res.get("url"));
            String url = urlObj != null ? urlObj.toString() : null;
            String publicId = Objects.toString(res.get("public_id"), null);

            cand.setCvLink(url);
            cand.setCvPublicId(publicId);
            cand.setUpdatedAt(new Date());
            cand = repo.save(cand);

            return mapper.toDto(cand);
        } catch (Exception e) {
            throw new RuntimeException("Upload CV failed: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional
    public void deleteCv(Integer candidateId) {
        Candidate cand = repo.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found: " + candidateId));
        try {
            if (cand.getCvPublicId() != null && !cand.getCvPublicId().isBlank()) {
                storage.delete(cand.getCvPublicId());
            }
            cand.setCvLink(null);
            cand.setCvPublicId(null);
            cand.setUpdatedAt(new Date());
            repo.save(cand);
        } catch (Exception e) {
            throw new RuntimeException("Delete CV failed: " + e.getMessage(), e);
        }
    }
}
