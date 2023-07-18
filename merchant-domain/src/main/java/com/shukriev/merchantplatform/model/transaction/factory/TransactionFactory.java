package com.shukriev.merchantplatform.model.transaction.factory;

import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.transaction.*;

import java.util.UUID;

public final class TransactionFactory {
	private TransactionFactory() {
	}

	public static Transaction getTransaction(
			final UUID id,
			final NormalMerchant merchant,
			final Double amount,
			final TransactionStatusEnum status,
			final String customerEmail,
			final String customerPhone,
			final Transaction reference,
			final TransactionType transactionType) {
		return switch (transactionType) {
			case AUTHORIZE ->
					new AuthorizeTransaction(id, merchant, amount, status, customerEmail, customerPhone, reference);
			case CHARGE -> new ChargeTransaction(id, merchant, amount, status, customerEmail, customerPhone, reference);
			case REFUND -> new RefundTransaction(id, merchant, amount, status, customerEmail, customerPhone, reference);
			case REVERSAL ->
					new ReversalTransaction(id, merchant, amount, status, customerEmail, customerPhone, reference);
		};
	}
}
