package personal_finance_tracker.personal_finance_tracker.controllers;

import personal_finance_tracker.personal_finance_tracker.domain.Transaction;
import personal_finance_tracker.personal_finance_tracker.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        return transactionService.createTransaction(transaction);
    }

    @GetMapping("/history/{accountId}")
    public List<Transaction> getHistory(@PathVariable Long accountId) {
        return transactionService.getTransactionsByAccount(accountId);
    }

    @GetMapping("/paginated")
    public Page<Transaction> getAllTransactionsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("ASC") ? 
                    Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return transactionService.getAllTransactionsPaginated(pageable);
    }

    @GetMapping("/account/{accountId}/paginated")
    public Page<Transaction> getAccountTransactionsPaginated(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("ASC") ? 
                    Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return transactionService.getTransactionsByAccountPaginated(accountId, pageable);
    }

    @GetMapping("/user/{userId}/paginated")
    public Page<Transaction> getUserTransactionsPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("ASC") ? 
                    Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return transactionService.getTransactionsByUserPaginated(userId, pageable);
    }
} 
