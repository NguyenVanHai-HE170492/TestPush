package vn.edu.fpt.HRVinaPortal.controllers.recruitment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CandidateDto;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateCandidateRequest;
import vn.edu.fpt.HRVinaPortal.services.recruitment.CandidateService;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/api/candidates", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CandidateController {

    private final CandidateService service;

    @GetMapping
    public ResponseEntity<List<CandidateDto>> all() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateDto> one(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CandidateDto> create(@Valid @RequestBody CreateOrUpdateCandidateRequest req) {
        CandidateDto created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CandidateDto> update(@PathVariable Integer id,
                                               @Valid @RequestBody CreateOrUpdateCandidateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CandidateDto>> search(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(service.search(keyword));
    }

    // ✅ Lấy ứng viên theo JobPosting (đang dùng ở FE)
    @GetMapping("/by-job/{jobPostingId}")
    public ResponseEntity<List<CandidateDto>> byJob(@PathVariable Integer jobPostingId) {
        return ResponseEntity.ok(service.getByJobPosting(jobPostingId));
    }

    // (Optional) alias RESTful hơn /api/job-postings/{id}/candidates
    @GetMapping("/../job-postings/{jobPostingId}/candidates")
    public ResponseEntity<List<CandidateDto>> byJobAlias(@PathVariable Integer jobPostingId) {
        return ResponseEntity.ok(service.getByJobPosting(jobPostingId));
    }

    @PostMapping("/{candidateId}/apply/{jobPostingId}")
    public ResponseEntity<Void> apply(@PathVariable Integer candidateId,
                                      @PathVariable Integer jobPostingId) {
        service.applyJob(candidateId, jobPostingId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping(path = "/{id}/cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CandidateDto> uploadCv(
            @PathVariable Integer id,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(service.uploadCv(id, file));
    }

    @DeleteMapping("/{id}/cv")
    public ResponseEntity<Void> deleteCv(@PathVariable Integer id) {
        service.deleteCv(id);
        return ResponseEntity.noContent().build();
    }
}
