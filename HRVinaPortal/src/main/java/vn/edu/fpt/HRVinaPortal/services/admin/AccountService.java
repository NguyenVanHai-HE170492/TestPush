package vn.edu.fpt.HRVinaPortal.services.admin;

import vn.edu.fpt.HRVinaPortal.dto.admin.AccountDto;

import java.util.List;

public interface AccountService {

    AccountDto createAccount(AccountDto accountDto);

    AccountDto login(String companyEmail, String password);

    List<AccountDto> getAllAccounts();

    AccountDto getAccountById(Integer accountId);

    AccountDto updateAccount(Integer accountId, AccountDto accountDto);

    void deleteAccount(Integer accountId);

}

