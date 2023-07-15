package com.shukriev.merchantplatform.inbound.transaction;

import com.shukriev.merchantplatform.exception.transaction.TransactionNotFoundException;
import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
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
		final var transactionId = UUID.randomUUID();
		final var transaction = new AuthorizeTransaction(
				transactionId,
				new NormalMerchant(
						"some@mail.com",
						"some_name",
						"some_password",
						ActiveInactiveStatusEnum.ACTIVE,
						"some_description",
						1.0d
				),
				10.0,
				TransactionStatusEnum.APPROVED,
				"some@email.com",
				"+359123123123",
				null);

		// given
		when(transactionProvider.getById(transactionId)).thenReturn(Optional.of(transaction));
		// when
		final var result = transactionService.getById(transactionId);
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
		final var transaction = new AuthorizeTransaction(
				null,
				new NormalMerchant(
						merchantId,
						"some@mail.com",
						"some_name",
						"some_password",
						ActiveInactiveStatusEnum.ACTIVE,
						"some_description",
						1.0d
				),
				10.0,
				TransactionStatusEnum.APPROVED,
				"some@email.com",
				"+359123123123",
				null);

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
		final var transaction = new AuthorizeTransaction(
				UUID.randomUUID(),
				new NormalMerchant(
						UUID.randomUUID(),
						"some@mail.com",
						"some_name",
						"some_password",
						ActiveInactiveStatusEnum.ACTIVE,
						"some_description",
						1.0d
				),
				10.0,
				TransactionStatusEnum.APPROVED,
				"some@email.com",
				"+359123123123",
				null);

		// given
		when(transactionProvider.createTransaction(transaction)).thenReturn(transaction);
		// when
		final var result = transactionService.createTransaction(transaction);
		// then
		Assertions.assertEquals(transaction, result);
	}

	@Test
	void shouldUpdateTransactionSuccessfullyTest() {
		final var transaction = new AuthorizeTransaction(
				UUID.randomUUID(),
				new NormalMerchant(
						UUID.randomUUID(),
						"some@mail.com",
						"some_name",
						"some_password",
						ActiveInactiveStatusEnum.ACTIVE,
						"some_description",
						1.0d
				),
				10.0,
				TransactionStatusEnum.APPROVED,
				"some@email.com",
				"+359123123123",
				null);

		// given
		when(transactionProvider.updateMerchant(transaction)).thenReturn(transaction);
		// when
		final var result = transactionService.updateTransaction(transaction);
		// then
		Assertions.assertEquals(transaction, result);
	}
}
