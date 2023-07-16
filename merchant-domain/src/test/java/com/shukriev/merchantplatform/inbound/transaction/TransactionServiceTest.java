package com.shukriev.merchantplatform.inbound.transaction;

import com.shukriev.merchantplatform.common.MerchantData;
import com.shukriev.merchantplatform.common.TransactionData;
import com.shukriev.merchantplatform.exception.merchant.MerchantInactiveException;
import com.shukriev.merchantplatform.exception.transaction.TransactionNotFoundException;
import com.shukriev.merchantplatform.model.transaction.AuthorizeTransaction;
import com.shukriev.merchantplatform.model.transaction.TransactionStatusEnum;
import com.shukriev.merchantplatform.outbound.transaction.TransactionProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
	@Mock
	private TransactionProvider transactionProvider;

	private TransactionService transactionService;

	@BeforeEach
	public void init() {
		transactionService = new TransactionServiceImpl(transactionProvider);
	}

	@Test
	void shouldReturnValidAuthorizeTransactionByIdTest() {
		// given
		final var transaction = TransactionData.transaction;
		when(transactionProvider.getById(transaction.getId())).thenReturn(Optional.of(transaction));

		// when
		final var result = transactionService.getById(transaction.getId());
		// then
		Assertions.assertEquals(transaction, result);
	}

	@Test
	void shouldFailDuringGetTransactionByIdTest() {
		final var transactionId = UUID.randomUUID();
		// when -> then
		when(transactionProvider.getById(transactionId)).thenReturn(Optional.empty());
		Assertions.assertThrows(TransactionNotFoundException.class, () -> transactionService.getById(transactionId));

	}

	@Test
	void shouldReturnMerchantTransactionsTransactionByIdTest() {
		final var merchantId = UUID.randomUUID();
		final var transaction = TransactionData.transaction;

		// given
		when(transactionProvider.getMerchantTransactions(merchantId)).thenReturn(Set.of(transaction));
		// when
		final var result = transactionService.getMerchantTransactions(merchantId);
		// then
		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals(Set.of(transaction), result);
	}

	@Test
	void shouldCreateTransactionSuccessfullyTest() {
		// given
		final var transaction = TransactionData.transaction;
		when(transactionProvider.createTransaction(transaction)).thenReturn(transaction);

		// when
		final var result = transactionService.createTransaction(transaction);
		// then
		Assertions.assertEquals(transaction, result);
	}

	@Test
	void shouldFailToCreateTransactionDueToInactiveMerchantTest() {
		// given
		final var transaction = new AuthorizeTransaction(
				UUID.randomUUID(),
				MerchantData.inactiveMerchant,
				10.0,
				TransactionStatusEnum.APPROVED,
				"some@email.com",
				"+359123123123",
				null);

		// when - then
		Assertions.assertThrows(MerchantInactiveException.class, () -> transactionService.createTransaction(transaction));
	}

	@Test
	void shouldUpdateTransactionSuccessfullyTest() {
		// given
		final var transaction = TransactionData.transaction;
		when(transactionProvider.updateTransaction(transaction)).thenReturn(transaction);
		// when
		final var result = transactionService.updateTransaction(transaction);
		// then
		Assertions.assertEquals(transaction, result);
	}
}
