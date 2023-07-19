package com.shukriev.merchantplatform.exception.transaction;

public final class TransactionNotFoundException extends RuntimeException {
	public TransactionNotFoundException(final String message) {
		super(message);
	}
}
