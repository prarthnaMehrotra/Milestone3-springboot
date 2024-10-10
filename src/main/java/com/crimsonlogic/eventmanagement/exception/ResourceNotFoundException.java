package com.crimsonlogic.eventmanagement.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -7073495464779606734L;

	public ResourceNotFoundException(String message) {
		super(message);
	}
	
}
