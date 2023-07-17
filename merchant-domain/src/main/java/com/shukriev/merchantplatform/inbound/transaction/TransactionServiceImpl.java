package com.shukriev.merchantplatform.inbound.transaction;

import com.shukriev.merchantplatform.exception.merchant.MerchantInactiveException;
import com.shukriev.merchantplatform.exception.merchant.MerchantNotFoundException;
import com.shukriev.merchantplatform.exception.transaction.TransactionNotFoundException;
import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.transaction.*;
import com.shukriev.merchantplatform.outbound.merchant.MerchantProvider;
import com.shukriev.merchantplatform.outbound.transaction.TransactionProvider;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public final class TransactionServiceImpl implements TransactionService {
	private final TransactionProvider transactionProvider;
	private final MerchantProvider merchantProvider;

	public TransactionServiceImpl(final TransactionProvider transactionProvider,
								  final MerchantProvider merchantProvider) {
		this.transactionProvider = transactionProvider;
		this.merchantProvider = merchantProvider;
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
	public Transaction createTransaction(final Transaction transaction) {
		//Validate transaction reference for reach Transaction Type
		transaction.validateReferenceTransaction();

		if (ActiveInactiveStatusEnum.INACTIVE.equals(transaction.getMerchant().getStatus())) {
			throw new MerchantInactiveException("Failed to create transaction, the provided merchant is inactive", transaction.getMerchant().getId());
		}

		//There is nothing to handle in the Authorization Transaction we just store it
		if (ChargeTransaction.class.equals(transaction.getClass())) {
			handleChargeTransaction(
					transaction.getStatus(),
					transaction.getMerchant().getId(),
					transaction.getAmount());
		} else if (RefundTransaction.class.equals(transaction.getClass())) {
			handleRefundTransaction(
					transaction.getId(),
					transaction.getStatus(),
					transaction.getAmount(),
					transaction.getMerchant().getId(),
					transaction.getReference().getId());
		} else if (ReversalTransaction.class.equals(transaction.getClass())) {
			handleReversalTransaction(
					transaction.getId(),
					transaction.getReference().getId());
		}

		return transactionProvider.createTransaction(transaction);
	}

	@Override
	public void deleteByTimestampBefore(LocalDateTime dateTime) {
		transactionProvider.deleteByTimestampBefore(dateTime);
	}

	/***
	 * The side effect of handling Charge Transaction is updating merchant's totalTransactionSum
	 * Throws MerchantNotFoundException when the Transaction Merchant is not found
	 * @param transactionStatus - to validate if the transaction status is Approved
	 * @param merchantId - MerchantId to update
	 * @param amount - Amount to update
	 */
	private void handleChargeTransaction(final TransactionStatusEnum transactionStatus,
										 final UUID merchantId,
										 final Double amount) {
		if (TransactionStatusEnum.APPROVED.equals(transactionStatus)) {
			merchantProvider.getById(merchantId)
					.map(m -> m.updateTotalTransactionSum(amount))
					.map(m -> merchantProvider.updateMerchant((NormalMerchant) m))
					.orElseThrow(() -> new MerchantNotFoundException(MessageFormat.format("Failed to find Merchant with id: {0}", merchantId)));
		}
	}

	/***
	 * Handling Refund transactions
	 * We need to update the reference transaction (Which has to be Charge Transaction) status to Refunded
	 * If the Refund transaction status is Approved then the transaction amount has to be subtracted from the Merchant's total transaction sum
	 * @param transactionId - Transaction UUID
	 * @param transactionStatus - Transaction Status to validate
	 * @param amount - Amount to be subtracted
	 * @param referenceId - Reference UUID to update it is status to refunded
	 */
	private void handleRefundTransaction(final UUID transactionId,
										 final TransactionStatusEnum transactionStatus,
										 final Double amount,
										 final UUID merchantId,
										 final UUID referenceId) {
		transactionProvider.getById(referenceId)
				.map(rt -> rt.updateStatus(TransactionStatusEnum.REFUNDED))
				.map(transactionProvider::updateTransaction)
				.orElseThrow(() -> new TransactionNotFoundException(
						MessageFormat.format("Reference transaction with id {0} not found for parent transaction with id: ", referenceId, transactionId)));

		if (TransactionStatusEnum.APPROVED.equals(transactionStatus)) {
			merchantProvider.getById(merchantId)
					.map(m -> (NormalMerchant) m.updateTotalTransactionSum(-amount))
					.map(merchantProvider::updateMerchant)
					.orElseThrow(() -> new MerchantNotFoundException(MessageFormat.format("Failed to find Merchant with id: {0}", merchantId)));
		}
	}

	private void handleReversalTransaction(final UUID transactionId, final UUID referenceId) {
		transactionProvider.getById(referenceId)
				.map(t -> t.updateStatus(TransactionStatusEnum.REVERSED))
				.map(transactionProvider::updateTransaction)
				.orElseThrow(() -> new TransactionNotFoundException(
						MessageFormat.format("Reference transaction with id {0} not found for parent transaction with id: ", referenceId, transactionId)));
	}
}
