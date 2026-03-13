package personal_finance_tracker.personal_finance_tracker.repository;

import personal_finance_tracker.personal_finance_tracker.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
}
