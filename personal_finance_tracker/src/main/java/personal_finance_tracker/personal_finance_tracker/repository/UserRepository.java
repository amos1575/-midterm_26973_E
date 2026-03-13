package personal_finance_tracker.personal_finance_tracker.repository;

import personal_finance_tracker.personal_finance_tracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    
    // Find users by location code
    @Query("SELECT u FROM User u WHERE u.location.code = :code")
    List<User> findByLocationCode(@Param("code") String code);
    
    // Find users by location name
    @Query("SELECT u FROM User u WHERE u.location.name = :name")
    List<User> findByLocationName(@Param("name") String name);
}
