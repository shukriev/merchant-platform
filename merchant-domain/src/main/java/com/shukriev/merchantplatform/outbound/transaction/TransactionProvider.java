package com.shukriev.merchantplatform.outbound.transaction;

import com.shukriev.merchantplatform.model.transaction.Transaction;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TransactionProvider {
	@NotNull
	Optional<Transaction> getById(final UUID id);

	@NotNull
	Set<Transaction> getMerchantTransactions(final UUID merchantId);

	@NotNull
	Transaction updateTransaction(final Transaction transaction);

	@NotNull
	Transaction createTransaction(final Transaction transaction);
}
