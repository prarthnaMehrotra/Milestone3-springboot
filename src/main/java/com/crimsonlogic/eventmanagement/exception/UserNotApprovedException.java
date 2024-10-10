package com.crimsonlogic.eventmanagement.exception;

public class UserNotApprovedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UserNotApprovedException(String message) {
        super(message);
    }

}