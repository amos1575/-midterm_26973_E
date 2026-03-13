package personal_finance_tracker.personal_finance_tracker.repository;

import personal_finance_tracker.personal_finance_tracker.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Find all accounts belonging to a specific user
    List<Account> findByUserId(Long userId);
}
