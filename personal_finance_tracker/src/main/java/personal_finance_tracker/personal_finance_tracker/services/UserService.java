package personal_finance_tracker.personal_finance_tracker.services;

import personal_finance_tracker.personal_finance_tracker.domain.User;
import personal_finance_tracker.personal_finance_tracker.domain.Category;
import personal_finance_tracker.personal_finance_tracker.repository.UserRepository;
import personal_finance_tracker.personal_finance_tracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public List<User> getUsersByLocationCode(String code) {
        return userRepository.findByLocationCode(code);
    }

    public List<User> getUsersByLocationName(String name) {
        return userRepository.findByLocationName(name);
    }

    public User addFavoriteCategory(Long userId, Long categoryId) {
        User user = userRepository.findById(userId).orElseThrow();
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        user.getFavoriteCategories().add(category);
        return userRepository.save(user);
    }

    public User removeFavoriteCategory(Long userId, Long categoryId) {
        User user = userRepository.findById(userId).orElseThrow();
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        user.getFavoriteCategories().remove(category);
        return userRepository.save(user);
    }
}
