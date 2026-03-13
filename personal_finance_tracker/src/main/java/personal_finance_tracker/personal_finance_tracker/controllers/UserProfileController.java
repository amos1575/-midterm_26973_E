package personal_finance_tracker.personal_finance_tracker.controllers;

import personal_finance_tracker.personal_finance_tracker.domain.UserProfile;
import personal_finance_tracker.personal_finance_tracker.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @PostMapping
    public UserProfile createUserProfile(@RequestBody UserProfile userProfile) {
        return userProfileService.saveUserProfile(userProfile);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long userId) {
        return userProfileService.getUserProfileByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exists/user/{userId}")
    public boolean checkProfileExists(@PathVariable Long userId) {
        return userProfileService.existsByUserId(userId);
    }

    @PutMapping("/{id}")
    public UserProfile updateUserProfile(@PathVariable Long id, @RequestBody UserProfile userProfile) {
        return userProfileService.updateUserProfile(id, userProfile);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<UserProfile> updateUserProfileByUserId(@PathVariable Long userId, @RequestBody UserProfile userProfile) {
        UserProfile updated = userProfileService.updateUserProfileByUserId(userId, userProfile);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }
}
