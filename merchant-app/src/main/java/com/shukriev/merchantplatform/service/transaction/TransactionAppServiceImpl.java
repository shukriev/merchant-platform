package com.shukriev.merchantplatform.service.transaction;

import com.shukriev.merchantplatform.controller.transaction.dto.CreateTransactionDTO;
import com.shukriev.merchantplatform.controller.transaction.dto.DetailedTransactionDTO;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.inbound.transaction.TransactionService;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.transaction.factory.TransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionAppServiceImpl implements TransactionAppService {
	private final TransactionService transactionService;
	private final MerchantService merchantService;

	@Autowired
	public TransactionAppServiceImpl(final TransactionService transactionService,
									 final MerchantService merchantService) {
		this.transactionService = transactionService;
		this.merchantService = merchantService;
	}

	@Override
	@Transactional
	public DetailedTransactionDTO createTransaction(CreateTransactionDTO createTransactionDTO) {
		final var merchant = (NormalMerchant) merchantService.getById(createTransactionDTO.merchantId());
		final var transaction = TransactionFactory.getTransaction(
				null, //Initially the created transaction is with empty ID
				merchant,
				createTransactionDTO.amount(),
				createTransactionDTO.status(),
				createTransactionDTO.customerEmail(),
				createTransactionDTO.customerPhone(),
				Optional.ofNullable(createTransactionDTO.referenceId()).map(transactionService::getById).orElse(null),
				createTransactionDTO.transactionType()
		);
		final var createdTransaction = transactionService.createTransaction(transaction);

		return DetailedTransactionDTO.of(createdTransaction);
	}
}
