package personal_finance_tracker.personal_finance_tracker.services;

import personal_finance_tracker.personal_finance_tracker.domain.Location;
import personal_finance_tracker.personal_finance_tracker.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public Location save(Location location) {
        return locationRepository.save(location);
    }

    public List<Location> findChildren(UUID parentId) {
        return locationRepository.findAll(); 
    }
}
