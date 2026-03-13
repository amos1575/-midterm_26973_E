package personal_finance_tracker.personal_finance_tracker.services;

import personal_finance_tracker.personal_finance_tracker.domain.Category;
import personal_finance_tracker.personal_finance_tracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }
}
