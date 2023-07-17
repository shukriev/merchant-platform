package com.shukriev.merchantplatform.service.transaction;

import com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest;
import com.shukriev.merchantplatform.controller.transaction.dto.DetailedTransactionDTO;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.model.transaction.factory.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Optional;

import static com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest.MerchantData.merchant;

class TransactionAppServiceTest extends MerchantPlatformIntegrationTest {
	@Container
	private static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:11.1")
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
	private MerchantService merchantService;

	@Autowired
	private TransactionAppService transactionAppService;

	@Test
	void shouldCreateTransactionSuccessfullyTest() {
		// Given
		final var createdMerchant = merchantService.createMerchant(merchant);
		final var transactionToCreate = TransactionData.getCreateTransactionDTO(TransactionType.AUTHORIZE, createdMerchant);

		// When
		final var createdTransaction = transactionAppService.createTransaction(transactionToCreate);

		// Then
		Assertions.assertNotNull(createdTransaction);
		Assertions.assertNotNull(createdTransaction.id());
		Assertions.assertAll("Transactions Are Equal",
				() -> Assertions.assertEquals(transactionToCreate.transactionType(), createdTransaction.transactionType()),
				() -> Assertions.assertEquals(transactionToCreate.referenceId(), Optional.ofNullable(createdTransaction.reference()).map(DetailedTransactionDTO::id).orElse(null)),
				() -> Assertions.assertEquals(transactionToCreate.merchantId(), createdTransaction.merchant().id()),
				() -> Assertions.assertEquals(transactionToCreate.amount(), createdTransaction.amount()),
				() -> Assertions.assertEquals(transactionToCreate.status(), createdTransaction.status()),
				() -> Assertions.assertEquals(transactionToCreate.customerEmail(), createdTransaction.customerEmail()),
				() -> Assertions.assertEquals(transactionToCreate.customerPhone(), createdTransaction.customerPhone())
		);
	}
	
	//TODO Cover with more test scenarios including failures
}
