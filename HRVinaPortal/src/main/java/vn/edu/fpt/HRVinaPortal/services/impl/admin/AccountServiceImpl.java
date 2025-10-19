package vn.edu.fpt.HRVinaPortal.services.impl.admin;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.HRVinaPortal.dto.admin.AccountDto;
import vn.edu.fpt.HRVinaPortal.entities.admin.Account;
import vn.edu.fpt.HRVinaPortal.entities.admin.Role;
import vn.edu.fpt.HRVinaPortal.mappers.admin.AccountMapper;
import vn.edu.fpt.HRVinaPortal.repositories.admin.AccountRepository;
import vn.edu.fpt.HRVinaPortal.repositories.admin.RoleRepository;
import vn.edu.fpt.HRVinaPortal.services.admin.AccountService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService , UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public AccountDto login(String email, String password) {
        Account account = accountRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản với email '" + email + "' không tồn tại."));

        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new BadCredentialsException("Mật khẩu không chính xác.");
        }

        return AccountMapper.toDto(account);
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        if (accountRepository.existsByCompanyEmail(accountDto.getCompanyEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        // Chuyển đổi DTO sang Entity
        Account account = AccountMapper.toEntity(accountDto);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setCreatedAt(LocalDate.now());
        // BƯỚC QUAN TRỌNG: Lấy thông tin người dùng đang đăng nhập và gán vào 'createdBy'
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String creatorUsername;
        if (principal instanceof UserDetails) {
            // Lấy email (username) từ UserDetails của Spring Security
            creatorUsername = ((UserDetails) principal).getUsername();
        } else {
            // Fallback trong trường hợp principal là một chuỗi
            creatorUsername = principal.toString();
        }
        // Gán email của người tạo vào trường createdBy
        account.setCreatedBy(creatorUsername);
        // Lưu tài khoản mới vào DB
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(AccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto getAccountById(Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return AccountMapper.toDto(account);
    }

    @Override
    @Transactional
    public AccountDto updateAccount(Integer accountId, AccountDto accountDto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        if (accountDto.getCompanyEmail() != null && !accountDto.getCompanyEmail().isEmpty()) {
            Optional<Account> existingAccountWithEmail = accountRepository.findByCompanyEmail(accountDto.getCompanyEmail());
            if (existingAccountWithEmail.isPresent() && !existingAccountWithEmail.get().getAccountId().equals(accountId)) {
                throw new IllegalArgumentException("Email '" + accountDto.getCompanyEmail() + "' is already in use.");
            }
            account.setCompanyEmail(accountDto.getCompanyEmail());
        }

        if (accountDto.getPassword() != null && !accountDto.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        }

        // BƯỚC 4: THÊM LOGIC CẬP NHẬT ROLE
        if (accountDto.getRole() != null && accountDto.getRole().getRoleId() != null) {
            Integer newRoleId = accountDto.getRole().getRoleId();
            Role newRole = roleRepository.findById(newRoleId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Role với ID: " + newRoleId));
            account.setRole(newRole); // Gán role mới cho tài khoản
        }

        account.setUpdatedAt(LocalDate.now());
        Account updatedAccount = accountRepository.save(account);

        return AccountMapper.toDto(updatedAccount);
    }

    @Override
    public void deleteAccount(Integer accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Account not found");
        }
        accountRepository.deleteById(accountId);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // "username" ở đây chính là companyEmail
        return accountRepository.findByCompanyEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + username));
    }
}
