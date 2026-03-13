package personal_finance_tracker.personal_finance_tracker.controllers;

import personal_finance_tracker.personal_finance_tracker.domain.Account;
import personal_finance_tracker.personal_finance_tracker.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountService.saveAccount(account);
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/user/{userId}")
    public List<Account> getUserAccounts(@PathVariable Long userId) {
        return accountService.getAccountsByUserId(userId);
    }

    @GetMapping("/user/{userId}/total-balance")
    public Map<String, Object> getTotalBalance(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        double totalBalance = accounts.stream()
            .mapToDouble(Account::getBalance)
            .sum();
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("totalAccounts", accounts.size());
        response.put("totalBalance", totalBalance);
        response.put("accounts", accounts);
        
        return response;
    }
}
