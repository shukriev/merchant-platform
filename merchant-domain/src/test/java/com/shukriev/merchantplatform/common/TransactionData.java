package com.shukriev.merchantplatform.common;

import com.shukriev.merchantplatform.model.transaction.AuthorizeTransaction;
import com.shukriev.merchantplatform.model.transaction.Transaction;
import com.shukriev.merchantplatform.model.transaction.TransactionStatusEnum;

import java.util.UUID;

public final class TransactionData {
	private TransactionData() {

	}

	public static final Transaction transaction = new AuthorizeTransaction(
			UUID.randomUUID(),
			MerchantData.merchant,
			10.0,
			TransactionStatusEnum.APPROVED,
			"some@email.com",
			"+359123123123",
			null);
}
