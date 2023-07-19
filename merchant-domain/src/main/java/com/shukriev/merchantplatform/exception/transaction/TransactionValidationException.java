package com.shukriev.merchantplatform.exception.transaction;

public final class TransactionValidationException extends RuntimeException {
	public TransactionValidationException(final String message) {
		super(message);
	}
}
