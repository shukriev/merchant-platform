package com.shukriev.merchantplatform.adapter.transaction;

import com.shukriev.merchantplatform.adapter.merchant.MerchantProviderImpl;
import com.shukriev.merchantplatform.MerchantInfraMain;
import com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.transaction.RefundTransaction;
import com.shukriev.merchantplatform.model.transaction.Transaction;
import com.shukriev.merchantplatform.model.transaction.TransactionStatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDateTime;

@SpringBootTest(classes = MerchantInfraMain.class)
class TransactionProviderTest extends MerchantPlatformIntegrationTest {
	@Container
	private final static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:11.1")
			.withDatabaseName("test")
			.withUsername("sa")
			.withPassword("sa");

	@DynamicPropertySource
	private static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgresqlContainer::getUsername);
		registry.add("spring.datasource.password", postgresqlContainer::getPassword);
	}

	@BeforeEach
	void beforeEach() {
		cleanDatabase(postgresqlContainer);
	}

	@Autowired
	private TransactionProviderImpl transactionProvider;

	@Autowired
	private MerchantProviderImpl merchantProvider;

	@Test
	void shouldCreateTransactionSuccessfullyTest() {
		//Given
		final var createdMerchant = (NormalMerchant) merchantProvider.createMerchant(MerchantData.merchant);
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
		final var createdMerchant = (NormalMerchant) merchantProvider.createMerchant(MerchantData.merchant);
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
		final var createdMerchant = (NormalMerchant) merchantProvider.createMerchant(MerchantData.merchant);
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
		final var createdMerchant = (NormalMerchant) merchantProvider.createMerchant(MerchantData.merchant);
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

	@Test
	void shouldDeleteTransactionsBeforeTheProvidedTimeSuccessfullyTest() {
		//Given
		final var createdMerchant = (NormalMerchant) merchantProvider.createMerchant(MerchantData.merchant);
		final var transaction = TransactionData.of(createdMerchant);
		final var t1 = transactionProvider.createTransaction(transaction);
		final var t2 = transactionProvider.createTransaction(transaction);
		final var t3 = transactionProvider.createTransaction(transaction);

		//When
		transactionProvider.deleteByTimestampBefore(LocalDateTime.now().plusMinutes(1));
		//Then
		//Assert T1 is deleted
		Assertions.assertTrue(transactionProvider.getById(t1.getId()).isEmpty());
		//Assert T2 is deleted
		Assertions.assertTrue(transactionProvider.getById(t2.getId()).isEmpty());
		//Assert T3 is deleted
		Assertions.assertTrue(transactionProvider.getById(t3.getId()).isEmpty());
	}
}
