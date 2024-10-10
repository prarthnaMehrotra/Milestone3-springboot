package com.crimsonlogic.eventmanagement.exception;

public class CurrentPasswordIncorrectException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CurrentPasswordIncorrectException(String message) {
        super(message);
    }
}