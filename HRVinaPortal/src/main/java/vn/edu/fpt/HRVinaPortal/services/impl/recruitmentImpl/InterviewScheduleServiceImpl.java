package vn.edu.fpt.HRVinaPortal.services.impl.recruitmentImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CandidateDto;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateInterviewRequest;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.InterviewScheduleDto;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Candidate;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.InterviewSchedule;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.JobPosting;
import vn.edu.fpt.HRVinaPortal.exception.recruitment.ResourceNotFoundException;
import vn.edu.fpt.HRVinaPortal.mappers.recruitment.InterviewScheduleMapper;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.CandidateRepository;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.InterviewScheduleRepository;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.JobPostingRepository;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.spec.InterviewScheduleSpecifications;
import vn.edu.fpt.HRVinaPortal.services.recruitment.InterviewScheduleService;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.EmailPreviewDto;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.SendEmailsRequest;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.SendEmailsResponse;
import vn.edu.fpt.HRVinaPortal.services.impl.recruitmentImpl.InterviewScheduleServiceImpl;
import vn.edu.fpt.HRVinaPortal.services.recruitment.MailService;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewScheduleServiceImpl implements InterviewScheduleService {

    private final InterviewScheduleRepository repo;
    private final JobPostingRepository jobRepo;
    private final CandidateRepository candidateRepo;
    private final InterviewScheduleMapper mapper;
    private final MailService mailService;
    // --------- helpers ----------
    private CandidateDto toCandDto(Candidate c){
        CandidateDto d = new CandidateDto();
        d.setCandidateId(c.getCandidateId());
        d.setFullName(c.getFullName());
        d.setEmail(c.getEmail());
        d.setPhone(c.getPhone());
        d.setStatus(c.getStatus());
        return d;
    }

    /** Đảm bảo tất cả candidate đều đã apply đúng JobPosting */
    private void ensureCandidatesAppliedToJob(Integer jobPostingId, List<Candidate> candidates) {
        if (candidates == null || candidates.isEmpty()) return;
        Set<Integer> allowed = candidateRepo
                .findDistinctByAppliedJobs_JobPostingId(jobPostingId)
                .stream().map(Candidate::getCandidateId)
                .collect(Collectors.toSet());
        List<Integer> invalid = candidates.stream()
                .map(Candidate::getCandidateId)
                .filter(id -> !allowed.contains(id))
                .collect(Collectors.toList());
        if (!invalid.isEmpty()) {
            throw new IllegalArgumentException(
                    "Các ứng viên sau chưa apply vào JobPosting " + jobPostingId + ": " + invalid
            );
        }
    }
    @Override
    @Transactional(readOnly = true)
    public List<EmailPreviewDto> buildEmailPreview(Integer interviewId, String companyName, String hotline) {
        InterviewSchedule e = repo.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found: " + interviewId));
        if (e.getCandidates() == null || e.getCandidates().isEmpty()) return Collections.emptyList();

        return e.getCandidates().stream().map(c -> {
            EmailPreviewDto dto = new EmailPreviewDto();
            dto.setCandidateId(c.getCandidateId());
            dto.setCandidateName(c.getFullName());
            dto.setToEmail(c.getEmail());
            dto.setSubject(InterviewEmailTemplateBuilder.defaultSubject(e, c));
            dto.setHtmlBody(InterviewEmailTemplateBuilder.defaultHtml(e, c, companyName, hotline));
            return dto;
        }).collect(Collectors.toList());
    }

    // ==== NEW: send ====
    @Override
    @Transactional
    public SendEmailsResponse sendEmailsForInterview(Integer interviewId, SendEmailsRequest req) {
        InterviewSchedule e = repo.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found: " + interviewId));

        if (req == null || req.getEmails() == null || req.getEmails().isEmpty()) {
            return new SendEmailsResponse(0, Collections.emptyList());
        }

        // Xác nhận candidate thuộc lịch
        Set<Integer> validCandIds = e.getCandidates() == null ? Collections.emptySet() :
                e.getCandidates().stream().map(Candidate::getCandidateId).collect(Collectors.toSet());

        List<MailService.MailItem> items = new ArrayList<>();
        List<Integer> sentIds = new ArrayList<>();

        for (EmailPreviewDto dto : req.getEmails()) {
            if (dto.getCandidateId() == null || !validCandIds.contains(dto.getCandidateId())) {
                continue; // bỏ qua người không thuộc lịch
            }
            if (dto.getToEmail() == null || dto.getToEmail().isBlank()) {
                continue; // không có email
            }
            items.add(new MailService.MailItem(dto.getToEmail(), dto.getSubject(), dto.getHtmlBody()));
            sentIds.add(dto.getCandidateId());
        }

        mailService.sendHtmlBulk(items);
        return new SendEmailsResponse(sentIds.size(), sentIds);
    }

    // --------- applicant queries ----------
    @Override
    @Transactional(readOnly = true)
    public List<CandidateDto> getApplicantsByJobPosting(Integer jobPostingId) {
        return candidateRepo.findDistinctByAppliedJobs_JobPostingId(jobPostingId)
                .stream().map(this::toCandDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateDto> getApplicantsByJobPostingOfCompany(Integer companyId, Integer jobPostingId) {
        JobPosting job = jobRepo.findById(jobPostingId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting not found: " + jobPostingId));
        if (!job.getCompany().getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("JobPosting " + jobPostingId + " does not belong to company " + companyId);
        }
        return getApplicantsByJobPosting(jobPostingId);
    }

    // --------- CRUD ----------
    @Override
    @Transactional(readOnly = true)
    public List<InterviewScheduleDto> getAll() {
        return repo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewScheduleDto getById(Integer id) {
        InterviewSchedule e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found: " + id));
        return mapper.toDto(e);
    }

    private void validateCreate(CreateOrUpdateInterviewRequest r){
        if (r.getStartTime() == null)
            throw new IllegalArgumentException("startTime is required");
        if (r.getDuration() == null && r.getEndTime() == null)
            throw new IllegalArgumentException("Either duration or endTime is required");
        if (r.getDuration() != null && r.getDuration() <= 0)
            throw new IllegalArgumentException("duration must be > 0");
        if (r.getEndTime() != null && r.getEndTime().before(r.getStartTime()))
            throw new IllegalArgumentException("endTime must be after startTime");
    }

    @Override
    @Transactional
    public InterviewScheduleDto create(CreateOrUpdateInterviewRequest r) {
        validateCreate(r);
        JobPosting job = jobRepo.findById(r.getJobPostingId())
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting not found: " + r.getJobPostingId()));

        List<Candidate> candidates = (r.getCandidateIds() == null || r.getCandidateIds().isEmpty())
                ? new ArrayList<>()
                : candidateRepo.findAllById(r.getCandidateIds());

        // (tùy chọn) ràng buộc ứng viên đã apply vào job của lịch
        ensureCandidatesAppliedToJob(job.getJobPostingId(), candidates);

        InterviewSchedule e = mapper.toEntityForCreate(r, job, candidates, r.getUserAction());

        if (e.getDuration() == null) {
            int minutes = (int)((r.getEndTime().getTime() - r.getStartTime().getTime()) / 60_000L);
            if (minutes <= 0) throw new IllegalArgumentException("Computed duration must be > 0");
            e.setDuration(minutes);
        }

        e = repo.save(e); // OWNING = InterviewSchedule -> chèn vào Candidate_InterviewSchedule nếu có candidates
        return mapper.toDto(e);
    }

    @Override
    @Transactional
    public InterviewScheduleDto update(Integer id, CreateOrUpdateInterviewRequest r) {
        InterviewSchedule e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found: " + id));

        JobPosting jobOrNull = null;
        // ❌ lỗi cũ: if (r.getJobPostingId) != null
        if (r.getJobPostingId() != null) {
            jobOrNull = jobRepo.findById(r.getJobPostingId())
                    .orElseThrow(() -> new ResourceNotFoundException("JobPosting not found: " + r.getJobPostingId()));
        }

        List<Candidate> candOrNull = null;
        if (r.getCandidateIds() != null) {
            candOrNull = candidateRepo.findAllById(r.getCandidateIds());
            ensureCandidatesAppliedToJob(
                    (jobOrNull != null ? jobOrNull.getJobPostingId() : e.getJobPosting().getJobPostingId()),
                    candOrNull
            );
        }

        mapper.updateEntityFromRequest(r, e, jobOrNull, candOrNull, r.getUserAction());

        if (e.getStartTime() == null)
            throw new IllegalArgumentException("startTime cannot be null");
        if (e.getDuration() == null) {
            if (r.getEndTime() != null) {
                int minutes = (int)((r.getEndTime().getTime() - e.getStartTime().getTime()) / 60_000L);
                if (minutes <= 0) throw new IllegalArgumentException("Computed duration must be > 0");
                e.setDuration(minutes);
            } else {
                throw new IllegalArgumentException("duration/endTime required");
            }
        }

        e = repo.save(e);
        return mapper.toDto(e);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("Interview not found: " + id);
        repo.deleteById(id);
    }

    // --------- Quick queries ----------
    @Override
    @Transactional(readOnly = true)
    public List<InterviewScheduleDto> getByJobPosting(Integer jobPostingId) {
        return repo.findByJobPosting_JobPostingId(jobPostingId).stream()
                .map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewScheduleDto> getByCandidate(Integer candidateId) {
        return repo.findByCandidate(candidateId).stream()
                .map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewScheduleDto> getByStartBetween(Date from, Date to) {
        return repo.findByStartBetween(from, to).stream()
                .map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InterviewScheduleDto> search(Date from, Date to, String status, String q,
                                             Integer jobPostingId, Integer candidateId, Integer companyId,
                                             Pageable pageable) {
        Specification<InterviewSchedule> spec =
                InterviewScheduleSpecifications.filter(from, to, status, q, jobPostingId, candidateId, companyId);
        return repo.findAll(spec, pageable).map(mapper::toDto);
    }

    // --------- Manage candidates of an interview ----------
    @Override
    @Transactional
    public InterviewScheduleDto setCandidates(Integer interviewId, List<Integer> candidateIds) {
        InterviewSchedule e = repo.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found: " + interviewId));

        List<Candidate> newCands = (candidateIds == null || candidateIds.isEmpty())
                ? new ArrayList<>()
                : candidateRepo.findAllById(candidateIds);

        // validate: chỉ nhận ứng viên đã apply đúng job của lịch
        Integer jobId = e.getJobPosting().getJobPostingId();
        ensureCandidatesAppliedToJob(jobId, newCands);

        // đồng bộ 2 chiều trong bộ nhớ + owning side -> sẽ ghi bảng nối
        e.setCandidatesSynced(newCands);
        e = repo.save(e);
        return mapper.toDto(e);
    }

    @Override
    @Transactional
    public InterviewScheduleDto removeCandidates(Integer interviewId, List<Integer> candidateIds) {
        InterviewSchedule e = repo.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found: " + interviewId));
        if (candidateIds == null || candidateIds.isEmpty()) return mapper.toDto(e);

        List<Candidate> toRemove = candidateRepo.findAllById(candidateIds);
        for (Candidate c : toRemove) e.removeCandidate(c);

        e = repo.save(e);
        return mapper.toDto(e);
    }

    @Override
    @Transactional
    public InterviewScheduleDto clearCandidates(Integer interviewId) {
        return setCandidates(interviewId, Collections.emptyList());
    }

    @Override
    @Transactional
    public InterviewScheduleDto addApplicantsOfJobPosting(Integer interviewId, Integer jobPostingId, boolean replace) {
        InterviewSchedule e = repo.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found: " + interviewId));

        Integer jobIdOfInterview = e.getJobPosting().getJobPostingId();
        Integer jobId = (jobPostingId == null) ? jobIdOfInterview : jobPostingId;

        if (!jobIdOfInterview.equals(jobId)) {
            throw new IllegalArgumentException("Lịch " + interviewId + " thuộc JobPosting " + jobIdOfInterview
                    + " không khớp job yêu cầu " + jobId);
        }

        List<Candidate> applicants = candidateRepo.findDistinctByAppliedJobs_JobPostingId(jobId);

        if (replace) {
            ensureCandidatesAppliedToJob(jobId, applicants);
            e.setCandidatesSynced(applicants);
        } else {
            // merge không trùng
            for (Candidate c : applicants) e.addCandidate(c);
        }

        e = repo.save(e);
        return mapper.toDto(e);
    }

    @Override
    @Transactional
    public InterviewScheduleDto addCandidates(Integer interviewId, List<Integer> candidateIds) {
        InterviewSchedule e = repo.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found: " + interviewId));

        if (candidateIds == null || candidateIds.isEmpty()) return mapper.toDto(e);

        List<Candidate> toAdd = candidateRepo.findAllById(candidateIds);
        ensureCandidatesAppliedToJob(e.getJobPosting().getJobPostingId(), toAdd);

        for (Candidate c : toAdd) e.addCandidate(c);

        e = repo.save(e);
        return mapper.toDto(e);
    }

    @Override
    @Transactional
    public InterviewScheduleDto removeCandidate(Integer interviewId, Integer candidateId) {
        InterviewSchedule e = repo.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found: " + interviewId));
        Candidate c = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found: " + candidateId));

        e.removeCandidate(c);
        e = repo.save(e);
        return mapper.toDto(e);
    }

    // --------- bulk scheduling ----------
    @Override
    @Transactional
    public List<Integer> bulkScheduleFromApplicants(Integer jobPostingId,
                                                    Date startTimeFirstSlot,
                                                    Integer durationMinutes,
                                                    Integer gapMinutes,
                                                    String location,
                                                    String status,
                                                    String notes,
                                                    String actor) {

        if (startTimeFirstSlot == null) throw new IllegalArgumentException("startTimeFirstSlot is required");
        if (durationMinutes == null || durationMinutes <= 0) throw new IllegalArgumentException("durationMinutes must be > 0");
        if (location == null || location.isBlank()) throw new IllegalArgumentException("location is required");

        JobPosting job = jobRepo.findById(jobPostingId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting not found: " + jobPostingId));

        List<Candidate> applicants = candidateRepo.findDistinctByAppliedJobs_JobPostingId(jobPostingId);
        if (applicants.isEmpty()) return Collections.emptyList();

        long start = startTimeFirstSlot.getTime();
        long durMs = durationMinutes.longValue() * 60_000L;
        long gapMs = (gapMinutes == null ? 0 : gapMinutes.longValue() * 60_000L);

        List<Integer> createdIds = new ArrayList<>();

        for (int i = 0; i < applicants.size(); i++) {
            Candidate cand = applicants.get(i);

            InterviewSchedule e = new InterviewSchedule();
            e.setJobPosting(job);
            e.setStartTime(new Date(start + i * (durMs + gapMs)));
            e.setDuration(durationMinutes);
            e.setLocation(location);
            e.setStatus(status);
            e.setNotes((notes == null ? "" : notes) + " | Candidate: " + cand.getFullName());
            e.setCreatedBy(actor);
            e.setCreatedAt(new Date());

            // đặt candidate cho từng lịch (owning side)
            e.setCandidatesSynced(Collections.singletonList(cand));

            e = repo.save(e);
            createdIds.add(e.getInterviewId());
        }

        return createdIds;
    }
}
