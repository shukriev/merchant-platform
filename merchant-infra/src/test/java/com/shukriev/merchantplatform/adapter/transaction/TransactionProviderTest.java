package com.shukriev.merchantplatform.adapter.transaction;

import com.shukriev.merchantplatform.MerchantInfraMain;
import com.shukriev.merchantplatform.adapter.merchant.MerchantProviderImpl;
import com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest;
import com.shukriev.merchantplatform.model.transaction.RefundTransaction;
import com.shukriev.merchantplatform.model.transaction.Transaction;
import com.shukriev.merchantplatform.model.transaction.TransactionStatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MerchantInfraMain.class)
class TransactionProviderTest extends MerchantPlatformIntegrationTest {
	@Autowired
	private TransactionProviderImpl transactionProvider;

	@Autowired
	private MerchantProviderImpl merchantProvider;

	@Test
	void shouldCreateTransactionSuccessfullyTest() {
		//Given
		final var createdMerchant = merchantProvider.createMerchant(MerchantData.merchant);
		final var transaction = TransactionData.of(createdMerchant);
		//When
		final var createdTransaction = transactionProvider.createTransaction(transaction);
		//Then
		Assertions.assertNotNull(createdTransaction);
		//Validate that the UUID is generated
		Assertions.assertNotNull(createdTransaction.getId());
	}

	@Test
	void shouldReturnTransactionByIdSuccessfullyTest() {
		//Given
		final var createdMerchant = merchantProvider.createMerchant(MerchantData.merchant);
		final var transaction = TransactionData.of(createdMerchant);
		final Transaction createdTransaction = transactionProvider.createTransaction(transaction);
		//When
		final var selectedTransaction = transactionProvider.getById(createdTransaction.getId());
		//Then
		Assertions.assertTrue(selectedTransaction.isPresent());
		//Validate if selected transaction is same as the created one
		Assertions.assertEquals(createdTransaction, selectedTransaction.get());
	}

	@Test
	void shouldReturnMerchantTransactionsSuccessfullyTest() {
		//Given
		final var createdMerchant = merchantProvider.createMerchant(MerchantData.merchant);
		final var transaction = TransactionData.of(createdMerchant);
		final Transaction createdTransaction = transactionProvider.createTransaction(transaction);
		//When
		final var selectedTransaction = transactionProvider.getMerchantTransactions(createdMerchant.getId());
		//Then
		Assertions.assertEquals(1, selectedTransaction.size());
		Assertions.assertEquals(createdTransaction, selectedTransaction.toArray()[0]);
	}

	@Test
	void shouldUpdateTransactionsSuccessfullyTest() {
		//Given
		final var createdMerchant = merchantProvider.createMerchant(MerchantData.merchant);
		final var transaction = TransactionData.of(createdMerchant);
		final var createdTransaction = transactionProvider.createTransaction(transaction);
		final var updateTransaction = new RefundTransaction(
				createdTransaction.getId(),
				createdTransaction.getMerchant(),
				createdTransaction.getAmount(),
				TransactionStatusEnum.REFUNDED,
				"some@email.com",
				"+359123123123",
				null);

		//When
		final var updatedTransaction = transactionProvider.updateTransaction(updateTransaction);
		//Then
		Assertions.assertNotNull(updatedTransaction);
		Assertions.assertEquals(updateTransaction, updatedTransaction);
	}
}
