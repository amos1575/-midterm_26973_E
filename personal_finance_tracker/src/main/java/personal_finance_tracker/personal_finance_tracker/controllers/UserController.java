package personal_finance_tracker.personal_finance_tracker.controllers;

import personal_finance_tracker.personal_finance_tracker.domain.User;
import personal_finance_tracker.personal_finance_tracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userService.saveUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/exists/email/{email}")
    public boolean checkEmailExists(@PathVariable String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/exists/username/{username}")
    public boolean checkUsernameExists(@PathVariable String username) {
        return userService.existsByUsername(username);
    }

    @GetMapping("/location/code/{code}")
    public List<User> getUsersByLocationCode(@PathVariable String code) {
        return userService.getUsersByLocationCode(code);
    }

    @GetMapping("/location/name/{name}")
    public List<User> getUsersByLocationName(@PathVariable String name) {
        return userService.getUsersByLocationName(name);
    }

    @PostMapping("/{userId}/favorites/{categoryId}")
    public User addFavoriteCategory(@PathVariable Long userId, @PathVariable Long categoryId) {
        return userService.addFavoriteCategory(userId, categoryId);
    }

    @DeleteMapping("/{userId}/favorites/{categoryId}")
    public User removeFavoriteCategory(@PathVariable Long userId, @PathVariable Long categoryId) {
        return userService.removeFavoriteCategory(userId, categoryId);
    }
}
