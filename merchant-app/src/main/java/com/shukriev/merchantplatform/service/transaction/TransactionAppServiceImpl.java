package com.shukriev.merchantplatform.service.transaction;

import com.shukriev.merchantplatform.controller.transaction.dto.CreateTransactionDTO;
import com.shukriev.merchantplatform.controller.transaction.dto.DetailedTransactionDTO;
import com.shukriev.merchantplatform.exception.merchant.MerchantNotFoundException;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.inbound.transaction.TransactionService;
import com.shukriev.merchantplatform.model.merchant.Merchant;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.transaction.factory.TransactionFactory;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
		final var merchant = Objects.nonNull(createTransactionDTO.merchantId()) ?
			(NormalMerchant) merchantService.getById(createTransactionDTO.merchantId())
			: (NormalMerchant) getLoggedMerchant();
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

	@Override
	public Set<DetailedTransactionDTO> getMerchantTransactions() {
		final var merchant = getLoggedMerchant();
		return transactionService.getMerchantTransactions(merchant.getId())
				.stream().map(DetailedTransactionDTO::of)
				.collect(Collectors.toSet());
	}

	private Merchant getLoggedMerchant() {
		final var authentication = SecurityContextHolder.getContext().getAuthentication();
		final var userDetails = (UserDetails) authentication.getPrincipal();
		final var userEmail = userDetails.getUsername();
		//Instead of doing select here I can implement custom UserDetails object and pass the id and other needed info
		return merchantService.getByEmail(userEmail)
				.orElseThrow(() -> new MerchantNotFoundException(
						MessageFormat.format("Merchant with email {0} not found", userEmail)));
	}
}
