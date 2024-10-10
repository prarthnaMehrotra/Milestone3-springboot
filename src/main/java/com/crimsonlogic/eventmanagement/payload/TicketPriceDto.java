package com.crimsonlogic.eventmanagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketPriceDto {
	
	private String ticketPriceId;
	private String priceCategory;
	private double price;
	private String eventId;

}
