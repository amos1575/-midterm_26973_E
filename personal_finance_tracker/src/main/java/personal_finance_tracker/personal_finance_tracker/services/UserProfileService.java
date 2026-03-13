package personal_finance_tracker.personal_finance_tracker.services;

import personal_finance_tracker.personal_finance_tracker.domain.UserProfile;
import personal_finance_tracker.personal_finance_tracker.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile saveUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    public Optional<UserProfile> getUserProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }

    public boolean existsByUserId(Long userId) {
        return userProfileRepository.existsByUserId(userId);
    }

    public UserProfile updateUserProfile(Long id, UserProfile userProfile) {
        userProfile.setId(id);
        return userProfileRepository.save(userProfile);
    }

    public UserProfile updateUserProfileByUserId(Long userId, UserProfile userProfile) {
        Optional<UserProfile> existing = userProfileRepository.findByUserId(userId);
        if (existing.isPresent()) {
            UserProfile profile = existing.get();
            profile.setBio(userProfile.getBio());
            profile.setDateOfBirth(userProfile.getDateOfBirth());
            profile.setOccupation(userProfile.getOccupation());
            profile.setProfilePictureUrl(userProfile.getProfilePictureUrl());
            return userProfileRepository.save(profile);
        }
        return null;
    }
}
