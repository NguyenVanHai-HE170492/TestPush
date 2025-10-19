package vn.edu.fpt.HRVinaPortal.entities.recruitment;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "JobPosting")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JobPostingID")
    private Integer jobPostingId;

    @Column(name = "JobName", length = 150, nullable = false)
    private String jobName;

    @Column(name = "Address", length = 200)
    private String address;

    @Column(name = "Salary", length = 100)
    private String salary;

    @Column(name = "WorkType", length = 50)
    private String workType;

    @Column(name = "JobDescription", length = 1000)
    private String jobDescription;

    @Column(name = "JobRequirement", length = 1000)
    private String jobRequirement;

    @Column(name = "Benefits", length = 500)
    private String benefits;

    @Column(name = "Status", length = 50)
    private String status;

    @Temporal(TemporalType.DATE)
    @Column(name = "StartDate")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "EndDate")
    private Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CompanyID", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JobTitleCompanyID", nullable = false)
    private JobTitleCompany jobTitleCompany;

    @Column(name = "CreatedBy", length = 50)
    private String createdBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "CreatedAt")
    private Date createdAt;

    @Column(name = "UpdatedBy", length = 50)
    private String updatedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "UpdatedAt")
    private Date updatedAt;

    // mappedBy ở phía JobPosting (KHÔNG sở hữu)
    @ManyToMany(mappedBy = "appliedJobs")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Candidate> candidates;

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<InterviewSchedule> interviewSchedules;
}
