package com.shukriev.merchantplatform.controller.transaction.dto;

import com.shukriev.merchantplatform.controller.merchant.dto.DetailedMerchantDTO;
import com.shukriev.merchantplatform.model.transaction.Transaction;
import com.shukriev.merchantplatform.model.transaction.TransactionStatusEnum;
import com.shukriev.merchantplatform.model.transaction.factory.TransactionType;

import java.util.Optional;
import java.util.UUID;

public record DetailedTransactionDTO(UUID id, TransactionType transactionType, DetailedMerchantDTO merchant,
									 Double amount, TransactionStatusEnum status, String customerEmail,
									 String customerPhone, DetailedTransactionDTO reference) {

	public static DetailedTransactionDTO of(Transaction transaction) {
		return new DetailedTransactionDTO(
				transaction.getId(),
				TransactionType.fromValue(transaction.getTransactionType()),
				Optional.ofNullable(transaction.getMerchant()).map(DetailedMerchantDTO::of).orElse(null),
				transaction.getAmount(),
				transaction.getStatus(),
				transaction.getCustomerEmail(),
				transaction.getCustomerPhone(),
				Optional.ofNullable(transaction.getReference()).map(DetailedTransactionDTO::of).orElse(null));
	}
}
