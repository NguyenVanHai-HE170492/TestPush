package vn.edu.fpt.HRVinaPortal.entities.admin;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName; // e.g. Admin, Manager, Staff

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDate.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDate.now();
    }
}
