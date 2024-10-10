package com.crimsonlogic.eventmanagement.exception;

public class InsufficientWalletBalanceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InsufficientWalletBalanceException(String message) {
        super(message);
    }
}