package vn.edu.fpt.HRVinaPortal.entities.recruitment;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Candidate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CandidateID")
    @EqualsAndHashCode.Include
    private Integer candidateId;

    @Column(name = "FullName", length = 100, nullable = false)
    private String fullName;

    @Column(name = "Gender")
    private Boolean gender;

    @Temporal(TemporalType.DATE)
    @Column(name = "BirthDate")
    private Date birthDate;

    @Column(name = "Address", length = 200)
    private String address;

    @Column(name = "Email", length = 50)
    private String email;

    @Column(name = "Phone", length = 10)
    private String phone;

    @Column(name = "CVLink", length = 500)
    private String cvLink;

    @Column(name = "CVPublicId", length = 255)
    private String cvPublicId;

    @Column(name = "Status", length = 50)
    private String status;

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

    // ===== N-N với JobPosting (OWNING SIDE = Candidate) =====
    @ManyToMany
    @JoinTable(
            name = "candidate_jobPosting",
            joinColumns = @JoinColumn(
                    name = "CandidateID",
                    referencedColumnName = "CandidateID",
                    foreignKey = @ForeignKey(name = "fk_cand_job_cand")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "JobPostingID",
                    referencedColumnName = "JobPostingID",
                    foreignKey = @ForeignKey(name = "fk_cand_job_job")
            )
    )
    @ToString.Exclude
    @Builder.Default
    private List<JobPosting> appliedJobs = new ArrayList<>();

    // ===== N-N với InterviewSchedule (INVERSE SIDE = mappedBy) =====
    // Owning side đã chuyển sang InterviewSchedule
    @ManyToMany(mappedBy = "candidates")
    @ToString.Exclude
    @Builder.Default
    private List<InterviewSchedule> interviews = new ArrayList<>();
}
