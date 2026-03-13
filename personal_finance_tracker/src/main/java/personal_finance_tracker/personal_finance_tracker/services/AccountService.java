package personal_finance_tracker.personal_finance_tracker.services;

import personal_finance_tracker.personal_finance_tracker.domain.Account;
import personal_finance_tracker.personal_finance_tracker.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}
