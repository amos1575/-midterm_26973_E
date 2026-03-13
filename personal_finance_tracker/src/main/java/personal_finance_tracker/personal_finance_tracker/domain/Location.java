package personal_finance_tracker.personal_finance_tracker.domain;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class Location {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String code;
	private String name;
	private String type;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Location parentLocation;

	@OneToMany(mappedBy = "parentLocation")
	private List<Location> subLocations;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Location getParentLocation() {
		return parentLocation;
	}

	public void setParentLocation(Location parentLocation) {
		this.parentLocation = parentLocation;
	}

	public List<Location> getSubLocations() {
		return subLocations;
	}

	public void setSubLocations(List<Location> subLocations) {
		this.subLocations = subLocations;
	}
}
