package com.shukriev.merchantplatform.service.transaction;

import com.shukriev.merchantplatform.controller.transaction.dto.CreateTransactionDTO;
import com.shukriev.merchantplatform.controller.transaction.dto.DetailedTransactionDTO;

public interface TransactionAppService {
	DetailedTransactionDTO createTransaction(final CreateTransactionDTO createTransactionDTO);
}
