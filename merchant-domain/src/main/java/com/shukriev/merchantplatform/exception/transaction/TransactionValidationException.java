package com.shukriev.merchantplatform.exception.transaction;

public final class TransactionValidationException extends RuntimeException {
	public TransactionValidationException(String message) {
		super(message);
	}
}
