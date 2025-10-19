package vn.edu.fpt.HRVinaPortal.entities.recruitment;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "InterviewSchedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InterviewSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InterviewID")
    @EqualsAndHashCode.Include
    private Integer interviewId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "StartTime", nullable = false)
    private Date startTime;

    @Column(name = "Duration", nullable = false)   // phút
    private Integer duration;

    @Column(name = "Location", length = 200)
    private String location;

    @Column(name = "Room", length = 50)
    private String room;

    @Column(name = "Status", length = 50)
    private String status;

    @Column(name = "Notes", length = 1000)
    private String notes;

    @Column(name = "CreatedBy", length = 50)
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CreatedAt")
    private Date createdAt;

    @Column(name = "UpdatedBy", length = 50)
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UpdatedAt")
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JobPostingID", nullable = false)
    private JobPosting jobPosting;

    // ===== N-N với Candidate (OWNING SIDE = InterviewSchedule) =====
    // Đặt JoinTable ở đây để khi save(interview) -> ghi vào bảng nối
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "candidate_interviewSchedule", // nếu DB của bạn là 'candidate_interviewschedule' thì đổi lại tên này
            joinColumns = @JoinColumn(
                    name = "InterviewID",
                    referencedColumnName = "InterviewID",
                    foreignKey = @ForeignKey(name = "fk_cand_inter_inter")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "CandidateID",
                    referencedColumnName = "CandidateID",
                    foreignKey = @ForeignKey(name = "fk_cand_inter_cand")
            )
    )
    @ToString.Exclude
    @Builder.Default
    private List<Candidate> candidates = new ArrayList<>();

    // ===== Helper để đồng bộ hai chiều trong bộ nhớ (khuyến nghị dùng) =====
    public void addCandidate(Candidate c) {
        if (c == null) return;
        if (!this.candidates.contains(c)) this.candidates.add(c);
        if (c.getInterviews() == null) c.setInterviews(new ArrayList<>());
        if (!c.getInterviews().contains(this)) c.getInterviews().add(this);
    }

    public void removeCandidate(Candidate c) {
        if (c == null) return;
        this.candidates.remove(c);
        if (c.getInterviews() != null) c.getInterviews().remove(this);
    }

    public void setCandidatesSynced(List<Candidate> newList) {
        // clear old links
        if (this.candidates != null) {
            for (Candidate old : new ArrayList<>(this.candidates)) {
                removeCandidate(old);
            }
        }
        if (newList != null) {
            for (Candidate c : newList) addCandidate(c);
        }
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) createdAt = new Date();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = new Date();
    }
}
