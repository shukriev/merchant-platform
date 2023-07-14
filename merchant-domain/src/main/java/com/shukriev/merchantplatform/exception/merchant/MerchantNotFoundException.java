package com.shukriev.merchantplatform.exception.merchant;

public final class MerchantNotFoundException extends RuntimeException {
	public MerchantNotFoundException(final String message) {
		super(message);
	}
}
