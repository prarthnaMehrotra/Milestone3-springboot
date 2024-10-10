package com.crimsonlogic.eventmanagement.exception;

public class UnauthorizedAccessException extends RuntimeException {

	private static final long serialVersionUID = -9190572093999502667L;

	public UnauthorizedAccessException(String message) {
        super(message);
    }
}
