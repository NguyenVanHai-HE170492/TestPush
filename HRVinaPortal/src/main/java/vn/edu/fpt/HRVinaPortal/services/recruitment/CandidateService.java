package vn.edu.fpt.HRVinaPortal.services.recruitment;
import org.springframework.web.multipart.MultipartFile;

import vn.edu.fpt.HRVinaPortal.dto.recruitment.*;
import java.util.List;

public interface CandidateService {
    List<CandidateDto> getAll();
    CandidateDto getById(Integer id);
    CandidateDto create(CreateOrUpdateCandidateRequest req);
    CandidateDto update(Integer id, CreateOrUpdateCandidateRequest req);
    void delete(Integer id);
    List<CandidateDto> search(String keyword);

    void applyJob(Integer candidateId, Integer jobPostingId);

    // ✅ new
    List<CandidateDto> getByJobPosting(Integer jobPostingId);
    CandidateDto uploadCv(Integer candidateId, MultipartFile file);
    void deleteCv(Integer candidateId);
}
