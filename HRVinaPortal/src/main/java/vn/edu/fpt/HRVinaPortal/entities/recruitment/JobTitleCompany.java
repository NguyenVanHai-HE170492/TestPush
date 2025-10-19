package vn.edu.fpt.HRVinaPortal.entities.recruitment;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "job_title_company")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class JobTitleCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_title_companyid")
    private Integer jobTitleCompanyId;

    @Column(name = "title_name", length = 100, nullable = false)
    private String titleName;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyid", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "jobTitleCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<JobPosting> jobPostings;
}
