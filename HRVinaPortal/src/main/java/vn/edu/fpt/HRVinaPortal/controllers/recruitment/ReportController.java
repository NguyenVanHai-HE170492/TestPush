package vn.edu.fpt.HRVinaPortal.controllers.recruitment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.HRVinaPortal.repositories.recruitment.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    private final CompanyRepository companyRepo;
    private final JobPostingRepository jobRepo;
    private final CandidateRepository candRepo;
    private final InterviewScheduleRepository interviewRepo;

    @GetMapping("/recruitment-summary")
    public ResponseEntity<Map<String, Object>> recruitmentSummary() {
        Map<String, Object> res = new HashMap<>();
        res.put("totalCompanies", companyRepo.count());
        res.put("totalJobs", jobRepo.count());
        res.put("totalCandidates", candRepo.count());
        res.put("totalInterviews", interviewRepo.count());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/company-stats")
    public ResponseEntity<List<Map<String, Object>>> companyStats() {
        List<Map<String, Object>> data = companyRepo.findAll().stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("companyId", c.getCompanyId());
            m.put("companyName", c.getCompanyName());
            long jobCount = jobRepo.countByCompany_CompanyId(c.getCompanyId());
            long interviewCount = interviewRepo.findAll().stream()
                    .filter(i -> i.getJobPosting() != null && i.getJobPosting().getCompany().getCompanyId().equals(c.getCompanyId()))
                    .count();
            long candCount = jobRepo.findByCompany_CompanyId(c.getCompanyId()).stream()
                    .mapToLong(j -> (j.getCandidates() != null) ? j.getCandidates().size() : 0)
                    .sum();
            m.put("jobs", jobCount);
            m.put("candidates", candCount);
            m.put("interviews", interviewCount);
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(data);
    }

    @GetMapping("/candidate-status")
    public ResponseEntity<List<Map<String, Object>>> candidateStatus() {
        Map<String, Long> map = candRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        c -> Optional.ofNullable(c.getStatus()).orElse("Không xác định"),
                        Collectors.counting()
                ));

        List<Map<String, Object>> result = map.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("status", e.getKey());
                    m.put("count", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/interview-status")
    public ResponseEntity<List<Map<String, Object>>> interviewStatus() {
        Map<String, Long> map = interviewRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        i -> Optional.ofNullable(i.getStatus()).orElse("Không xác định"),
                        Collectors.counting()
                ));

        List<Map<String, Object>> result = map.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("status", e.getKey());
                    m.put("count", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

}
