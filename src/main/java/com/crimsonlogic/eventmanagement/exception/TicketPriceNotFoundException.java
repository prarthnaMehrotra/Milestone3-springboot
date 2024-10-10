package com.crimsonlogic.eventmanagement.exception;

public class TicketPriceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TicketPriceNotFoundException(String message) {
        super(message);
    }
}