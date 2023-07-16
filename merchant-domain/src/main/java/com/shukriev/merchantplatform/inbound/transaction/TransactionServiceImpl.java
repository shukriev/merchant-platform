package com.shukriev.merchantplatform.inbound.transaction;

import com.shukriev.merchantplatform.exception.merchant.MerchantInactiveException;
import com.shukriev.merchantplatform.exception.transaction.TransactionNotFoundException;
import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.transaction.Transaction;
import com.shukriev.merchantplatform.outbound.transaction.TransactionProvider;

import java.text.MessageFormat;
import java.util.Set;
import java.util.UUID;

public final class TransactionServiceImpl implements TransactionService {
	private final TransactionProvider transactionProvider;

	public TransactionServiceImpl(final TransactionProvider transactionProvider) {
		this.transactionProvider = transactionProvider;
	}

	@Override
	public Transaction getById(UUID id) {
		return transactionProvider.getById(id).orElseThrow(() ->
				new TransactionNotFoundException(MessageFormat.format("Transaction with id: {0} not found", id)));
	}

	@Override
	public Set<Transaction> getMerchantTransactions(UUID merchantId) {
		return transactionProvider.getMerchantTransactions(merchantId);
	}

	@Override
	public Transaction updateTransaction(Transaction transaction) {
		return transactionProvider.updateTransaction(transaction);
	}

	@Override
	public Transaction createTransaction(Transaction transaction) {

		if (ActiveInactiveStatusEnum.INACTIVE.equals(transaction.getMerchant().getStatus())) {
			throw new MerchantInactiveException("Failed to create transaction, the provided merchant is inactive", transaction.getMerchant().getId());
		}

		return transactionProvider.createTransaction(transaction);
	}
}
