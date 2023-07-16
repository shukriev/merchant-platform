package com.shukriev.merchantplatform.exception.merchant;

import java.text.MessageFormat;
import java.util.UUID;

public final class MerchantInactiveException extends RuntimeException {
	public MerchantInactiveException(String message, UUID merchantId) {
		super(MessageFormat.format("{0} | merchantId: {1}", message, merchantId));
	}
}
