package com.shukriev.merchantplatform.exception.transaction;

public final class TransactionNotFoundException extends RuntimeException {
	public TransactionNotFoundException(String message) {
		super(message);
	}
}
