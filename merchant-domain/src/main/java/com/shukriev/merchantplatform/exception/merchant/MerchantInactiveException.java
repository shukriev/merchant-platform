package com.shukriev.merchantplatform.exception.merchant;

import java.text.MessageFormat;
import java.util.UUID;
import com.shukriev.merchantplatform.model.transaction.Transaction;

public final class MerchantInactiveException extends RuntimeException {
	public MerchantInactiveException(final String message, final UUID merchantId) {
		super(MessageFormat.format("{0} | merchantId: {1}", message, merchantId));
	}
}
