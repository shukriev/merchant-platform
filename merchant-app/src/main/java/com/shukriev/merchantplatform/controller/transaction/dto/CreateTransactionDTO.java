package com.shukriev.merchantplatform.controller.transaction.dto;

import com.shukriev.merchantplatform.model.transaction.TransactionStatusEnum;
import com.shukriev.merchantplatform.model.transaction.factory.TransactionType;

import java.util.UUID;

public record CreateTransactionDTO(TransactionType transactionType, UUID merchantId, Double amount, TransactionStatusEnum status,
								   String customerEmail, String customerPhone, UUID referenceId) {
}
