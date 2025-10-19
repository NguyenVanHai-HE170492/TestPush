// src/main/java/vn/edu/fpt/HRVinaPortal/repositories/recruitment/JobPostingRepository.java
package vn.edu.fpt.HRVinaPortal.repositories.recruitment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.TopJobPostingDto;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.JobPosting;

import java.util.Date;
import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPosting, Integer> {

    @Query("""
        SELECT jp FROM JobPosting jp
        WHERE lower(jp.jobName) LIKE lower(concat('%', :kw, '%'))
           OR lower(jp.address) LIKE lower(concat('%', :kw, '%'))
           OR lower(jp.jobDescription) LIKE lower(concat('%', :kw, '%'))
    """)
    List<JobPosting> search(@Param("kw") String keyword);

    List<JobPosting> findByCompany_CompanyId(Integer companyId);
    List<JobPosting> findByJobTitleCompany_JobTitleCompanyId(Integer jobTitleCompanyId);

    @Query("SELECT jp FROM JobPosting jp WHERE jp.startDate>=:from AND jp.endDate<=:to")
    List<JobPosting> findByDateRange(@Param("from") Date from, @Param("to") Date to);

    long countByCompany_CompanyId(Integer companyId);

    // ⭐ Top N job có số ứng viên ứng tuyển nhiều nhất
    @Query("""
        SELECT new vn.edu.fpt.HRVinaPortal.dto.recruitment.TopJobPostingDto(
            jp.jobPostingId,
            jp.jobName,
            jp.company.companyName,
            jp.jobTitleCompany.titleName,
            COUNT(c)
        )
        FROM JobPosting jp
        LEFT JOIN jp.candidates c
        GROUP BY jp.jobPostingId, jp.jobName, jp.company.companyName, jp.jobTitleCompany.titleName
        ORDER BY COUNT(c) DESC
    """)
    List<TopJobPostingDto> findTopByApplicantCount(Pageable pageable);

    // ============== NEW: distinct filters ==============
    @Query("""
        SELECT DISTINCT jp.workType
        FROM JobPosting jp
        WHERE jp.workType IS NOT NULL AND jp.workType <> ''
        ORDER BY jp.workType ASC
    """)
    List<String> distinctWorkTypes();

    @Query("""
        SELECT DISTINCT jp.address
        FROM JobPosting jp
        WHERE jp.address IS NOT NULL AND jp.address <> ''
        ORDER BY jp.address ASC
    """)
    List<String> distinctLocations();

    // ============== NEW: advanced, pageable search ==============
    @Query("""
        SELECT jp FROM JobPosting jp
        WHERE (:kw IS NULL OR
              lower(concat(
                 coalesce(jp.jobName,''),' ',
                 coalesce(jp.jobDescription,''),' ',
                 coalesce(jp.address,''),' ',
                 coalesce(jp.workType,''))) LIKE lower(concat('%', :kw, '%')))
          AND (:titleId IS NULL OR jp.jobTitleCompany.jobTitleCompanyId = :titleId)
          AND (:companyId IS NULL OR jp.company.companyId = :companyId)
          AND (:location IS NULL OR lower(jp.address) LIKE lower(concat('%', :location, '%')))
          AND (:workType IS NULL OR jp.workType = :workType)
        ORDER BY jp.createdAt DESC, jp.jobPostingId DESC
    """)
    Page<JobPosting> searchAdvanced(
            @Param("kw") String keyword,
            @Param("titleId") Integer jobTitleCompanyId,
            @Param("companyId") Integer companyId,
            @Param("location") String location,
            @Param("workType") String workType,
            Pageable pageable
    );
}
