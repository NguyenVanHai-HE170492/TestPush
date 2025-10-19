package vn.edu.fpt.HRVinaPortal.repositories.recruitment;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.InterviewSchedule;

import java.util.Date;
import java.util.List;

public interface InterviewScheduleRepository
        extends JpaRepository<InterviewSchedule, Integer>, JpaSpecificationExecutor<InterviewSchedule> {

    List<InterviewSchedule> findByJobPosting_JobPostingId(Integer jobPostingId);

    @Query("SELECT i FROM InterviewSchedule i JOIN i.candidates c WHERE c.candidateId = :candidateId")
    List<InterviewSchedule> findByCandidate(@Param("candidateId") Integer candidateId);

    @Query("SELECT i FROM InterviewSchedule i WHERE i.startTime BETWEEN :from AND :to")
    List<InterviewSchedule> findByStartBetween(@Param("from") Date from, @Param("to") Date to);
}
