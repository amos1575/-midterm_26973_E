package personal_finance_tracker.personal_finance_tracker.repository;

import personal_finance_tracker.personal_finance_tracker.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByParentLocation(Location parentLocation);

    @Query("SELECT l FROM Location l WHERE l.parentLocation.id = :parentId")
    List<Location> findByParentId(@Param("parentId") Long parentId);
}