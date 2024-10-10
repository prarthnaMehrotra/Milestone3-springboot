package com.crimsonlogic.eventmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "venue")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Venue {
	
	@Id
	@Column(name = "venue_id", length = 10)
	private String venueId;
	
	@Column(name = "venue_location", length = 100)
	private String venueLocation;
	
	@Column(name = "maps_link", length = 255)
	private String mapsLink;
	
	@Column(name = "capacity")
	private int capacity;

	@OneToOne
	@JoinColumn(name = "venue_for_event")
	private Events venueForEvent;
}
