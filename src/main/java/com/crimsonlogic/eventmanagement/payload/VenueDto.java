package com.crimsonlogic.eventmanagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueDto {
	
	private String venueId;
	private String venueLocation;
	private String mapsLink;
	private int capacity;
	private String eventId;

}
