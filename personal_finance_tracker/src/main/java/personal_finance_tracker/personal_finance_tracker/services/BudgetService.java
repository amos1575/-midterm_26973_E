package personal_finance_tracker.personal_finance_tracker.services;

import personal_finance_tracker.personal_finance_tracker.domain.Budget;
import personal_finance_tracker.personal_finance_tracker.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget updateLimit(Long id, Double newLimit) {
        Budget budget = budgetRepository.findById(id).orElseThrow();
        budget.setAmountLimit(newLimit);
        return budgetRepository.save(budget);
    }
}
