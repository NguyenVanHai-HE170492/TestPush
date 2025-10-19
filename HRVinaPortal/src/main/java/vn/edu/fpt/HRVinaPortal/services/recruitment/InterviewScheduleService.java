// InterviewScheduleService.java
package vn.edu.fpt.HRVinaPortal.services.recruitment;

import vn.edu.fpt.HRVinaPortal.dto.recruitment.*;
import org.springframework.data.domain.*;

import java.util.Date;
import java.util.List;

public interface InterviewScheduleService {
    List<InterviewScheduleDto> getAll();

    InterviewScheduleDto getById(Integer id);

    InterviewScheduleDto create(CreateOrUpdateInterviewRequest req);

    InterviewScheduleDto update(Integer id, CreateOrUpdateInterviewRequest req);

    void delete(Integer id);

    List<InterviewScheduleDto> getByJobPosting(Integer jobPostingId);

    List<InterviewScheduleDto> getByCandidate(Integer candidateId);

    List<InterviewScheduleDto> getByStartBetween(Date from, Date to);

    // assign/remove candidate
    InterviewScheduleDto addCandidates(Integer interviewId, List<Integer> candidateIds);

    InterviewScheduleDto removeCandidate(Integer interviewId, Integer candidateId);

    // ===== NEW: Search & bulk scheduling =====
    Page<InterviewScheduleDto> search(Date from, Date to, String status, String q,
                                      Integer jobPostingId, Integer candidateId, Integer companyId,
                                      Pageable pageable);

    List<Integer> bulkScheduleFromApplicants(Integer jobPostingId,
                                             Date startTimeFirstSlot,
                                             Integer durationMinutes,
                                             Integer gapMinutes,
                                             String location,
                                             String status,
                                             String notes,
                                             String actor);

    List<CandidateDto> getApplicantsByJobPosting(Integer jobPostingId);

    List<CandidateDto> getApplicantsByJobPostingOfCompany(Integer companyId, Integer jobPostingId);
    // ===== NEW: quản lý danh sách ứng viên của 1 lịch =====

    /**
     * Ghi đè toàn bộ danh sách ứng viên của lịch bằng candidateIds
     */
    InterviewScheduleDto setCandidates(Integer interviewId, List<Integer> candidateIds);

    /**
     * Xoá hàng loạt theo danh sách candidateIds
     */
    InterviewScheduleDto removeCandidates(Integer interviewId, List<Integer> candidateIds);

    /**
     * Xoá toàn bộ ứng viên khỏi lịch
     */
    InterviewScheduleDto clearCandidates(Integer interviewId);

    /**
     * Thêm ứng viên đã nộp đơn của 1 JobPosting vào lịch.
     * - jobPostingId có thể null -> mặc định dùng job của chính lịch
     * - replace=true: ghi đè, false: merge/không trùng lặp
     */
    InterviewScheduleDto addApplicantsOfJobPosting(Integer interviewId, Integer jobPostingId, boolean replace);

    List<EmailPreviewDto> buildEmailPreview(Integer interviewId, String companyName, String hotline);
    SendEmailsResponse sendEmailsForInterview(Integer interviewId, SendEmailsRequest req);

}


