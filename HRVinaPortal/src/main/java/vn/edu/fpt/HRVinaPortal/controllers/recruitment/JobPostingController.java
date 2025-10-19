// src/main/java/vn/edu/fpt/HRVinaPortal/controllers/recruitment/JobPostingController.java
package vn.edu.fpt.HRVinaPortal.controllers.recruitment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.*;

import vn.edu.fpt.HRVinaPortal.services.recruitment.JobPostingService;

import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/api/job-postings", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService service;

    @GetMapping
    public ResponseEntity<List<JobPostingDto>> all() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostingDto> one(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobPostingDto> create(@Valid @RequestBody CreateOrUpdateJobPostingRequest req) {
        JobPostingDto created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobPostingDto> update(@PathVariable Integer id,
                                                @Valid @RequestBody CreateOrUpdateJobPostingRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @GetMapping("/top-applied")
    public ResponseEntity<List<TopJobPostingDto>> topApplied(
            @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit) {
        return ResponseEntity.ok(service.getTopByApplicants(limit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Search & filters
    @GetMapping("/search")
    public ResponseEntity<List<JobPostingDto>> search(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(service.search(keyword));
    }

    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<List<JobPostingDto>> byCompany(@PathVariable Integer companyId) {
        return ResponseEntity.ok(service.getByCompany(companyId));
    }

    @GetMapping("/by-job-title/{jobTitleCompanyId}")
    public ResponseEntity<List<JobPostingDto>> byTitle(@PathVariable Integer jobTitleCompanyId) {
        return ResponseEntity.ok(service.getByJobTitle(jobTitleCompanyId));
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<JobPostingDto>> byRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to) {
        return ResponseEntity.ok(service.getByDateRange(from, to));
    }

    @PostMapping("/{jobPostingId}/candidates/{candidateId}")
    public ResponseEntity<Void> attach(@PathVariable Integer jobPostingId,
                                       @PathVariable Integer candidateId) {
        service.attachCandidate(jobPostingId, candidateId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{jobPostingId}/candidates/{candidateId}")
    public ResponseEntity<Void> detach(@PathVariable Integer jobPostingId,
                                       @PathVariable Integer candidateId) {
        service.detachCandidate(jobPostingId, candidateId);
        return ResponseEntity.noContent().build();
    }

    // ===== NEW: filters source for FE =====
    @GetMapping("/filters")
    public ResponseEntity<JobFiltersDto> filters() {
        return ResponseEntity.ok(service.getFilters());
    }

    // ===== NEW: pageable advanced search =====
    @GetMapping("/query")
    public ResponseEntity<Page<JobPostingDto>> query(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer jobTitleCompanyId,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String workType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pr = PageRequest.of(Math.max(0, page), Math.max(1, size));
        return ResponseEntity.ok(
                service.searchAdvanced(keyword, jobTitleCompanyId, companyId, location, workType, pr)
        );
    }
}
