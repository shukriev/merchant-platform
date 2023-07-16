package com.shukriev.merchantplatform.inbound.transaction;

import com.shukriev.merchantplatform.model.transaction.Transaction;

import java.util.Set;
import java.util.UUID;

public interface TransactionService {
	Transaction getById(final UUID id);

	Set<Transaction> getMerchantTransactions(final UUID merchantId);

	Transaction updateTransaction(final Transaction transaction);

	Transaction createTransaction(final Transaction transaction);
}
