package com.crimsonlogic.eventmanagement.payload;

import lombok.Data;

@Data
public class BookingDto {
	
	private String bookingId;
	private String eventId;
	private String userId;
	private String eventName;
	private String ticketPriceId;
	private int noOfTickets;
	private String bookingStatus;
	private double totalPrice;
	private String fullName;
	private String location;
	
}
