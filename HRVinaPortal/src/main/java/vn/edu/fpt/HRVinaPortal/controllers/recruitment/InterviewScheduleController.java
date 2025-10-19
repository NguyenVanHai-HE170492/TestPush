package vn.edu.fpt.HRVinaPortal.controllers.recruitment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CandidateDto;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateInterviewRequest;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.InterviewScheduleDto;
import vn.edu.fpt.HRVinaPortal.services.recruitment.InterviewScheduleService;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.EmailPreviewDto;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.SendEmailsRequest;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.SendEmailsResponse;
import java.util.*;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class InterviewScheduleController {

    private final InterviewScheduleService service;

    // ===== CRUD =====
    @GetMapping
    public ResponseEntity<List<InterviewScheduleDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewScheduleDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ===== NEW: ghi đè danh sách ứng viên của lịch =====
    @PutMapping("/{interviewId}/candidates")
    public ResponseEntity<InterviewScheduleDto> setCandidates(
            @PathVariable Integer interviewId,
            @RequestBody List<Integer> candidateIds) {
        return ResponseEntity.ok(service.setCandidates(interviewId, candidateIds));
    }

    // ===== NEW: xoá hàng loạt theo danh sách id =====
    @DeleteMapping("/{interviewId}/candidates")
    public ResponseEntity<InterviewScheduleDto> removeCandidates(
            @PathVariable Integer interviewId,
            @RequestParam(name = "all", defaultValue = "false") boolean all,
            @RequestBody(required = false) List<Integer> candidateIds) {
        if (all) {
            return ResponseEntity.ok(service.clearCandidates(interviewId));
        }
        return ResponseEntity.ok(service.removeCandidates(interviewId, candidateIds == null ? List.of() : candidateIds));
    }

    // ===== NEW: thêm applicants của 1 JobPosting vào lịch (replace/merge) =====
    @PostMapping("/{interviewId}/candidates/by-job/{jobPostingId}")
    public ResponseEntity<InterviewScheduleDto> addApplicantsByJob(
            @PathVariable Integer interviewId,
            @PathVariable Integer jobPostingId,
            @RequestParam(defaultValue = "false") boolean replace) {
        return ResponseEntity.ok(service.addApplicantsOfJobPosting(interviewId, jobPostingId, replace));
    }

    // ===== NEW: thêm applicants theo "job của chính lịch" (tiện lợi) =====
    @PostMapping("/{interviewId}/candidates/by-job")
    public ResponseEntity<InterviewScheduleDto> addApplicantsByJobOfInterview(
            @PathVariable Integer interviewId,
            @RequestParam(defaultValue = "false") boolean replace) {
        return ResponseEntity.ok(service.addApplicantsOfJobPosting(interviewId, null, replace));
    }
    /**
     * Bước 1: HR bấm "Tiếp tục" sau khi tạo lịch -> xem trước email đề xuất cho từng ứng viên.
     * Query params optional: companyName, hotline để render trong template.
     */
    @GetMapping("/{interviewId}/email/preview")
    public ResponseEntity<List<EmailPreviewDto>> previewEmails(
            @PathVariable Integer interviewId,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String hotline
    ) {
        return ResponseEntity.ok(service.buildEmailPreview(interviewId, companyName, hotline));
    }

    /**
     * Bước 2: HR sửa tiêu đề/nội dung trên FE (nếu muốn), rồi gửi hàng loạt.
     */
    @PostMapping("/{interviewId}/email/send")
    public ResponseEntity<SendEmailsResponse> sendEmails(
            @PathVariable Integer interviewId,
            @RequestBody SendEmailsRequest body
    ) {
        return ResponseEntity.ok(service.sendEmailsForInterview(interviewId, body));
    }


    @PostMapping
    public ResponseEntity<InterviewScheduleDto> create(@RequestBody CreateOrUpdateInterviewRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterviewScheduleDto> update(@PathVariable Integer id,
                                                       @RequestBody CreateOrUpdateInterviewRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Filter nhanh =====
    @GetMapping("/by-job/{jobPostingId}")
    public ResponseEntity<List<InterviewScheduleDto>> byJob(@PathVariable Integer jobPostingId) {
        return ResponseEntity.ok(service.getByJobPosting(jobPostingId));
    }

    @GetMapping("/by-candidate/{candidateId}")
    public ResponseEntity<List<InterviewScheduleDto>> byCandidate(@PathVariable Integer candidateId) {
        return ResponseEntity.ok(service.getByCandidate(candidateId));
    }

    @GetMapping("/between")
    public ResponseEntity<List<InterviewScheduleDto>> between(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to) {
        return ResponseEntity.ok(service.getByStartBetween(from, to));
    }

    // ===== Search nâng cao (paging + sort) =====
    @GetMapping("/search")
    public ResponseEntity<Page<InterviewScheduleDto>> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "q") String locationOrNoteLike,
            @RequestParam(required = false) Integer jobPostingId,
            @RequestParam(required = false) Integer candidateId,
            @RequestParam(required = false) Integer companyId, // HRVina id -> lọc theo công ty
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startTime,asc") String sort // ví dụ: startTime,asc|desc
    ) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1])) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, s[0]));
        Page<InterviewScheduleDto> result = service.search(from, to, status, locationOrNoteLike, jobPostingId, candidateId, companyId, pageable);
        return ResponseEntity.ok(result);
    }

    // ===== manage candidates of an interview =====
    @PostMapping("/{interviewId}/candidates")
    public ResponseEntity<InterviewScheduleDto> addCandidates(
            @PathVariable Integer interviewId,
            @RequestBody List<Integer> candidateIds) {
        return ResponseEntity.ok(service.addCandidates(interviewId, candidateIds));
    }

    @DeleteMapping("/{interviewId}/candidates/{candidateId}")
    public ResponseEntity<InterviewScheduleDto> removeCandidate(
            @PathVariable Integer interviewId,
            @PathVariable Integer candidateId) {
        return ResponseEntity.ok(service.removeCandidate(interviewId, candidateId));
    }

    // ===== LÊN LỊCH HÀNG LOẠT cho ứng viên đã apply vào JobPosting của HRVina =====
    @PostMapping("/bulk-schedule/{jobPostingId}")
    public ResponseEntity<Map<String, Object>> bulkSchedule(
            @PathVariable Integer jobPostingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startTimeFirstSlot,
            @RequestParam Integer durationMinutes,
            @RequestParam(defaultValue = "0") Integer gapMinutes,
            @RequestParam String location,
            @RequestParam(defaultValue = "Scheduled") String status,
            @RequestParam(required = false) String notes,
            @RequestParam(defaultValue = "system") String actor
    ) {
        List<Integer> ids = service.bulkScheduleFromApplicants(jobPostingId,
                startTimeFirstSlot, durationMinutes, gapMinutes, location, status, notes, actor);

        Map<String, Object> res = new HashMap<>();
        res.put("created", ids.size());
        res.put("interviewIds", ids);
        return ResponseEntity.ok(res);
    }

    // Ứng viên đã apply vào 1 JobPosting (không ràng công ty)
    @GetMapping("/applicants/by-job/{jobPostingId}")
    public ResponseEntity<List<CandidateDto>> applicantsByJob(@PathVariable Integer jobPostingId) {
        return ResponseEntity.ok(service.getApplicantsByJobPosting(jobPostingId));
    }

    // Ứng viên của bài tuyển dụng thuộc 1 company cụ thể (ví dụ HRVina)
    @GetMapping("/applicants/by-company/{companyId}/job/{jobPostingId}")
    public ResponseEntity<List<CandidateDto>> applicantsByJobOfCompany(
            @PathVariable Integer companyId,
            @PathVariable Integer jobPostingId) {
        return ResponseEntity.ok(service.getApplicantsByJobPostingOfCompany(companyId, jobPostingId));
    }

    // Alias dành cho HRVina nếu bạn muốn đường dẫn ngắn gọn
    @GetMapping("/applicants/hrvina/job/{jobPostingId}")
    public ResponseEntity<List<CandidateDto>> applicantsOfHrvina(@PathVariable Integer jobPostingId) {
        final int HRVINA_ID = 1; // đổi nếu khác
        return ResponseEntity.ok(service.getApplicantsByJobPostingOfCompany(HRVINA_ID, jobPostingId));
    }
}

