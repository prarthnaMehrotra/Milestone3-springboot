package com.crimsonlogic.eventmanagement.payload;

import java.time.LocalTime;
import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
	
	private String eventId;
	private String eventName;
	private String description;
	private LocalDate date;
	private LocalTime time;
	private String imagePath;
	private Timestamp createdAt;
	private String createdBy;
	private String categoryId;
	
	private List<SponsorDto> sponsors;
    private List<TicketPriceDto> ticketPrices;
    private VenueDto venue;
    
    private int totalCapacity;
    private int bookedTickets;
	
}
