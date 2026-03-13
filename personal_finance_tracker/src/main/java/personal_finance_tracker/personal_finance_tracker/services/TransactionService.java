package personal_finance_tracker.personal_finance_tracker.services;

import personal_finance_tracker.personal_finance_tracker.domain.Account;
import personal_finance_tracker.personal_finance_tracker.domain.Transaction;
import personal_finance_tracker.personal_finance_tracker.repository.AccountRepository;
import personal_finance_tracker.personal_finance_tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Transaction createTransaction(Transaction transaction) {
        // Get the account
        Account account = transaction.getAccount();
        if (account != null && account.getId() != null) {
            // Fetch the full account from database
            Account fullAccount = accountRepository.findById(account.getId()).orElse(null);
            if (fullAccount != null) {
                // Deduct transaction amount from account balance
                fullAccount.setBalance(fullAccount.getBalance() - transaction.getAmount());
                // Save updated account
                accountRepository.save(fullAccount);
            }
        }
        // Save and return the transaction
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findAll(); 
    }

    public Page<Transaction> getTransactionsByAccountPaginated(Long accountId, Pageable pageable) {
        return transactionRepository.findByAccountId(accountId, pageable);
    }

    public Page<Transaction> getTransactionsByUserPaginated(Long userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable);
    }

    public Page<Transaction> getAllTransactionsPaginated(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }
}
