package vn.edu.fpt.HRVinaPortal.entities.recruitment;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "company")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "companyid")
    private Integer companyId;

    @Column(name = "company_name", length = 100, nullable = false)
    private String companyName;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

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

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<JobTitleCompany> jobTitles;


}
