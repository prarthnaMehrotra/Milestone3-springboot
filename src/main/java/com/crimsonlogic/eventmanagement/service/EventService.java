package com.crimsonlogic.eventmanagement.service;

import com.crimsonlogic.eventmanagement.payload.EventDto;
import com.crimsonlogic.eventmanagement.payload.SponsorDto;
import com.crimsonlogic.eventmanagement.payload.TicketPriceDto;
import com.crimsonlogic.eventmanagement.payload.VenueDto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface EventService {
	List<EventDto> getAllEvents();

	EventDto createEvent(String userDetailsId, EventDto eventDto, MultipartFile imageFile);

	void addSponsors(String eventId, SponsorDto sponsors);

	void addTicketPrices(String eventId, TicketPriceDto ticketPrices);

	void addVenue(String eventId, VenueDto venue);

	List<TicketPriceDto> getTicketPricesForEvent(String eventId);

	List<SponsorDto> getSponsorsForEvent(String eventId);

	VenueDto getVenueForEvent(String eventId);

	EventDto getEventDetails(String eventId);

	List<EventDto> getEventsByOrganizer(String userDetailsId);

	List<EventDto> getAllEventsByDate();

}
