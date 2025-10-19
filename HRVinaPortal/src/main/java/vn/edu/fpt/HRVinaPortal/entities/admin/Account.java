package vn.edu.fpt.HRVinaPortal.entities.admin;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "company_email", nullable = false, unique = true, length = 100)
    private String companyEmail;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    // N:1 relationship with Role
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Role role;

    // Ở đây chỉ lưu ID nhân viên, vì Employee có thể chưa tạo
    @Column(name = "employee_id")
    private Integer employeeId;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDate.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDate.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về quyền của người dùng, ví dụ: "ROLE_ADMIN", "ROLE_USER"
        return List.of(new SimpleGrantedAuthority(role.getRoleName()));
    }

    @Override
    public String getUsername() {
        // Spring Security sẽ sử dụng email làm "username" để định danh
        return this.companyEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Tài khoản không bao giờ hết hạn
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Tài khoản không bị khóa
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Mật khẩu không bao giờ hết hạn
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Tài khoản luôn được bật
        return true;
    }
}
