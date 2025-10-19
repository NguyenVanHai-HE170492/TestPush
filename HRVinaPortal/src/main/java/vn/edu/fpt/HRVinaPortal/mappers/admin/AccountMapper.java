package vn.edu.fpt.HRVinaPortal.mappers.admin;

import vn.edu.fpt.HRVinaPortal.entities.admin.Account;
import vn.edu.fpt.HRVinaPortal.dto.admin.AccountDto;

public class AccountMapper {

    public static AccountDto toDto(Account account) {
        if (account == null) return null;

        return AccountDto.builder()
                .accountId(account.getAccountId())
                .companyEmail(account.getCompanyEmail())
                .password(account.getPassword()) // ⚠️ có thể null khi trả response
                .createdBy(account.getCreatedBy())
                .createdAt(account.getCreatedAt())
                .updatedBy(account.getUpdatedBy())
                .updatedAt(account.getUpdatedAt())
                .employeeId(account.getEmployeeId())
                .role(RoleMapper.toDto(account.getRole()))
                .build();
    }

    public static Account toEntity(AccountDto dto) {
        if (dto == null) return null;

        Account account = new Account();
        account.setAccountId(dto.getAccountId());
        account.setCompanyEmail(dto.getCompanyEmail());
        account.setPassword(dto.getPassword());
        account.setCreatedBy(dto.getCreatedBy());
        account.setCreatedAt(dto.getCreatedAt());
        account.setUpdatedBy(dto.getUpdatedBy());
        account.setUpdatedAt(dto.getUpdatedAt());
        account.setEmployeeId(dto.getEmployeeId());
        account.setRole(RoleMapper.toEntity(dto.getRole()));
        return account;
    }
}
