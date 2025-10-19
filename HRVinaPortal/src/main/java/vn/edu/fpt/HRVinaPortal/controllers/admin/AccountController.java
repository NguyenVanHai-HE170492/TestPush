package vn.edu.fpt.HRVinaPortal.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.HRVinaPortal.dto.admin.AccountDto;
import vn.edu.fpt.HRVinaPortal.services.admin.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // Create account
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        AccountDto created = accountService.createAccount(accountDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Get all accounts
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        List<AccountDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    // Get account by ID
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Integer id) {
        AccountDto account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    // Update account
    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Integer id, @RequestBody AccountDto accountDto) {
        AccountDto updated = accountService.updateAccount(id, accountDto);
        return ResponseEntity.ok(updated);
    }

    // Delete account
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
