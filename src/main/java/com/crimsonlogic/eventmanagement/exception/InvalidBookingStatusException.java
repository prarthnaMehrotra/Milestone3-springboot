package com.crimsonlogic.eventmanagement.exception;

public class InvalidBookingStatusException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidBookingStatusException(String message) {
        super(message);
    }
}