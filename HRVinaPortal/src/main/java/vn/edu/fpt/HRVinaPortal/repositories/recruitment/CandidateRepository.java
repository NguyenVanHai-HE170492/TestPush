package vn.edu.fpt.HRVinaPortal.repositories.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Candidate;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Integer> {

    @Query("""
       SELECT c FROM Candidate c
       WHERE lower(c.fullName) LIKE lower(concat('%', :kw, '%'))
          OR lower(c.email)    LIKE lower(concat('%', :kw, '%'))
          OR lower(c.phone)    LIKE lower(concat('%', :kw, '%'))
    """)
    List<Candidate> search(@Param("kw") String keyword);

    // ✅ Lấy danh sách ứng viên theo JobPosting (native MySQL)
    @Query(value = """
        SELECT c.*
        FROM Candidate c
        JOIN Candidate_JobPosting cj ON cj.CandidateID = c.CandidateID
        WHERE cj.JobPostingID = :jobPostingId
    """, nativeQuery = true)
    List<Candidate> findByJobPostingIdNative(@Param("jobPostingId") Integer jobPostingId);
    List<Candidate> findDistinctByAppliedJobs_JobPostingId(Integer jobPostingId);
}
