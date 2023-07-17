package com.shukriev.merchantplatform.adapter.transaction;

import com.shukriev.merchantplatform.model.transaction.Transaction;
import com.shukriev.merchantplatform.outbound.transaction.TransactionProvider;
import com.shukriev.merchantplatform.repository.postgres.transaction.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class TransactionProviderImpl implements TransactionProvider {
	private final TransactionRepository transactionRepository;

	@Autowired
	public TransactionProviderImpl(final TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@Override
	public Optional<Transaction> getById(UUID id) {
		return transactionRepository.findById(id);
	}

	@Override
	public Set<Transaction> getMerchantTransactions(UUID merchantId) {
		return transactionRepository.findByMerchant_Id(merchantId);
	}

	@Override
	public Transaction updateTransaction(Transaction transaction) {
		transactionRepository.update(
				transaction.getId(),
				transaction.getMerchant(),
				transaction.getAmount(),
				transaction.getStatus(),
				transaction.getCustomerEmail(),
				transaction.getCustomerPhone(),
				transaction.getReference()
		);

		return transaction;
	}

	@Override
	public Transaction createTransaction(Transaction transaction) {
		return transactionRepository.save(transaction);
	}

	@Override
	public void deleteByTimestampBefore(LocalDateTime dateTime) {
		transactionRepository.deleteByCreatedAtBefore(dateTime);
	}
}
