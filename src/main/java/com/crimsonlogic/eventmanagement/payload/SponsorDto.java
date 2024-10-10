package com.crimsonlogic.eventmanagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SponsorDto {
	
	private String sponsorId;
	private String sponsorName;
	private String contactNumber;
	private String eventId;

}
