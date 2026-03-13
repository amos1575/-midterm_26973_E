package personal_finance_tracker.personal_finance_tracker.controllers;

import personal_finance_tracker.personal_finance_tracker.domain.Location;
import personal_finance_tracker.personal_finance_tracker.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping
    public Location createLocation(@RequestBody Location location) {
        return locationService.save(location);
    }

    @PutMapping("/{id}")
    public Location updateLocation(@PathVariable UUID id, @RequestBody Location location) {
        location.setId(id);
        return locationService.save(location);
    }

    @GetMapping("/sub-locations/{parentId}")
    public List<Location> getByParent(@PathVariable UUID parentId) {
        return locationService.findChildren(parentId);
    }
}
