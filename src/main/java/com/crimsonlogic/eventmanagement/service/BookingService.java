package com.crimsonlogic.eventmanagement.service;

import java.util.List;
import java.util.Map;

import com.crimsonlogic.eventmanagement.payload.BookingDto;

public interface BookingService {

	BookingDto bookTickets(String userId, String eventId, String ticketPriceId, int numberOfTickets);

	List<BookingDto> getUserBookings(String userId);

	void cancelBooking(String bookingId);

	Map<String, Object> getTotalRevenueAndTicketsForEvent(String eventId);

}
