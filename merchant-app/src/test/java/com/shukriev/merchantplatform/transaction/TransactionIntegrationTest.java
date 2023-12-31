package com.shukriev.merchantplatform.transaction;

import com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest;
import com.shukriev.merchantplatform.controller.authentication.dto.SignInRequest;
import com.shukriev.merchantplatform.controller.transaction.dto.CreateTransactionDTO;
import com.shukriev.merchantplatform.controller.transaction.dto.DetailedTransactionDTO;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.inbound.transaction.TransactionService;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.transaction.TransactionStatusEnum;
import com.shukriev.merchantplatform.model.transaction.factory.TransactionFactory;
import com.shukriev.merchantplatform.model.transaction.factory.TransactionType;
import com.shukriev.merchantplatform.service.security.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Optional;

import static com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest.MerchantData.defaultMerchant;
import static com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest.MerchantData.merchant;
import static io.restassured.RestAssured.given;

class TransactionIntegrationTest extends MerchantPlatformIntegrationTest {

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
	private TransactionService transactionService;

	@Autowired
	private AuthenticationService authenticationService;

	@Test
	void shouldCreateAuthorizeTransactionSuccessfullyTest() {
		final var createdMerchant = (NormalMerchant) merchantService.createMerchant(merchant);
		final var transactionToCreate = TransactionData.getCreateTransactionDTO(TransactionType.AUTHORIZE, createdMerchant);

		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();
		//when
		final var response = given()
				.header("Authorization", "Bearer " + token)
				.body(transactionToCreate)
				.accept("application/json")
				.contentType("application/json")
				.when()
				.post("/transactions")
				.then()
				.extract()
				.body()
				.jsonPath()
				.getObject(".", DetailedTransactionDTO.class);

		//then
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.id());
		Assertions.assertAll("Transactions Are Equal",
				() -> Assertions.assertEquals(transactionToCreate.transactionType(), response.transactionType()),
				() -> Assertions.assertEquals(transactionToCreate.referenceId(), Optional.ofNullable(response.reference()).map(DetailedTransactionDTO::id).orElse(null)),
				() -> Assertions.assertEquals(transactionToCreate.merchantId(), response.merchant().id()),
				() -> Assertions.assertEquals(transactionToCreate.amount(), response.amount()),
				() -> Assertions.assertEquals(transactionToCreate.status(), response.status()),
				() -> Assertions.assertEquals(transactionToCreate.customerEmail(), response.customerEmail()),
				() -> Assertions.assertEquals(transactionToCreate.customerPhone(), response.customerPhone())
		);
	}

	@Test
	void shouldCreateChargeTransactionSuccessfullyTest() {
		final var createdMerchant = (NormalMerchant) merchantService.createMerchant(defaultMerchant);
		final var authorizeTransaction = Optional.ofNullable(TransactionFactory
						.getTransaction(null,
								createdMerchant,
								100.0,
								TransactionStatusEnum.APPROVED,
								"someCustomer@email.com",
								"+359123123123",
								null,
								TransactionType.AUTHORIZE))
				.map(transactionService::createTransaction).orElseThrow();


		final var chargeTransactionToCreate = new CreateTransactionDTO(
				TransactionType.CHARGE,
				createdMerchant.getId(),
				100.0,
				TransactionStatusEnum.APPROVED,
				"some@email.com",
				"+359123123123",
				authorizeTransaction.getId());

		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();

		//when
		final var response = given()
				.header("Authorization", "Bearer " + token)
				.body(chargeTransactionToCreate)
				.accept("application/json")
				.contentType("application/json")
				.when()
				.post("/transactions")
				.then()
				.extract()
				.body()
				.jsonPath()
				.getObject(".", DetailedTransactionDTO.class);

		//then
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.id());
		//Assert if merchant total sum has increased with the transaction amount
		final var selectedMerchantToAssert = (NormalMerchant) merchantService.getById(createdMerchant.getId());
		Assertions.assertEquals(100.0, selectedMerchantToAssert.getTotalTransactionSum());
	}

	@Test
	void shouldCreateChargeTransactionWithoutUpdatingMerchantSumSuccessfullyTest() {
		final var createdMerchant = (NormalMerchant) merchantService.createMerchant(defaultMerchant);
		final var authorizeTransaction = Optional.ofNullable(TransactionFactory
						.getTransaction(null,
								createdMerchant,
								100.0,
								TransactionStatusEnum.APPROVED,
								"someCustomer@email.com",
								"+359123123123",
								null,
								TransactionType.AUTHORIZE))
				.map(transactionService::createTransaction).orElseThrow();


		final var chargeTransactionToCreate = new CreateTransactionDTO(
				TransactionType.CHARGE,
				createdMerchant.getId(),
				100.0,
				TransactionStatusEnum.ERROR,
				"some@email.com",
				"+359123123123",
				authorizeTransaction.getId());

		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();

		//when
		final var response = given()
				.header("Authorization", "Bearer " + token)
				.body(chargeTransactionToCreate)
				.accept("application/json")
				.contentType("application/json")
				.when()
				.post("/transactions")
				.then()
				.extract()
				.body()
				.jsonPath()
				.getObject(".", DetailedTransactionDTO.class);

		//then
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.id());
		//Assert that merchant sum is not updated due to invalid charge transaction status
		final var selectedMerchantToAssert = (NormalMerchant) merchantService.getById(createdMerchant.getId());
		Assertions.assertEquals(0.0, selectedMerchantToAssert.getTotalTransactionSum());
	}

	@Test
	void shouldCreateChargeTransactionFailsDueToBadReferenceTypeTest() {
		final var createdMerchant = merchantService.createMerchant(defaultMerchant);

		final var chargeTransactionToCreate = new CreateTransactionDTO(
				TransactionType.CHARGE,
				createdMerchant.getId(),
				100.0,
				TransactionStatusEnum.APPROVED,
				"some@email.com",
				"+359123123123",
				null);

		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();
		//when
		final var response = given()
				.header("Authorization", "Bearer " + token)
				.body(chargeTransactionToCreate)
				.accept("application/json")
				.contentType("application/json")
				.when()
				.post("/transactions");

		//then
		Assertions.assertNotNull(response);
		Assertions.assertEquals(400, response.getStatusCode());
		Assertions.assertEquals("Charge transaction should contain reference to Authorize transaction", response.print());
	}

	@Test
	void shouldCreateReversalTransactionSuccessfullyTest() {
		final var createdMerchant = (NormalMerchant) merchantService.createMerchant(defaultMerchant);
		final var authorizeTransaction = Optional.ofNullable(TransactionFactory
						.getTransaction(null,
								createdMerchant,
								100.0,
								TransactionStatusEnum.APPROVED,
								"someCustomer@email.com",
								"+359123123123",
								null,
								TransactionType.AUTHORIZE))
				.map(transactionService::createTransaction).orElseThrow();


		final var reversalTransactionToCreate = new CreateTransactionDTO(
				TransactionType.REVERSAL,
				createdMerchant.getId(),
				100.0,
				TransactionStatusEnum.APPROVED,
				"some@email.com",
				"+359123123123",
				authorizeTransaction.getId());

		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();
		//when
		final var response = given()
				.header("Authorization", "Bearer " + token)
				.body(reversalTransactionToCreate)
				.accept("application/json")
				.contentType("application/json")
				.when()
				.post("/transactions")
				.then()
				.extract()
				.body()
				.jsonPath()
				.getObject(".", DetailedTransactionDTO.class);

		//then
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.id());
		final var selectedAuthorizeTransactionToAssert = transactionService.getById(authorizeTransaction.getId());
		Assertions.assertEquals(TransactionStatusEnum.REVERSED, selectedAuthorizeTransactionToAssert.getStatus());
	}

	@Test
	void shouldCreateReversalTransactionFailsDueToBadReferenceTypeTest() {
		final var createdMerchant = merchantService.createMerchant(defaultMerchant);

		final var reversalTransactionToCreate = new CreateTransactionDTO(
				TransactionType.REVERSAL,
				createdMerchant.getId(),
				100.0,
				TransactionStatusEnum.APPROVED,
				"some@email.com",
				"+359123123123",
				null);

		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();
		//when
		final var response = given()
				.header("Authorization", "Bearer " + token)
				.body(reversalTransactionToCreate)
				.accept("application/json")
				.contentType("application/json")
				.when()
				.post("/transactions");

		//then
		Assertions.assertNotNull(response);
		Assertions.assertEquals(400, response.getStatusCode());
		Assertions.assertEquals("Reversal transaction should contain reference to Authorize transaction", response.print());
	}

	@Test
	void shouldCreateFullRefundTransactionSuccessfullyTest() {
		final var createdMerchant = (NormalMerchant) merchantService.createMerchant(defaultMerchant);
		final var authorizeTransaction = Optional.ofNullable(TransactionFactory
						.getTransaction(null,
								createdMerchant,
								100.0,
								TransactionStatusEnum.APPROVED,
								"someCustomer@email.com",
								"+359123123123",
								null,
								TransactionType.AUTHORIZE))
				.map(transactionService::createTransaction).orElseThrow();

		final var chargeTransactionToCreate = Optional.ofNullable(TransactionFactory
						.getTransaction(
								null,
								createdMerchant,
								100.0,
								TransactionStatusEnum.APPROVED,
								"some@email.com",
								"+359123123123",
								authorizeTransaction,
								TransactionType.CHARGE))
				.map(transactionService::createTransaction).orElseThrow();

		final var refundTransactionToCreate = new CreateTransactionDTO(
				TransactionType.REFUND,
				createdMerchant.getId(),
				100.0,
				TransactionStatusEnum.APPROVED,
				"some@email.com",
				"+359123123123",
				chargeTransactionToCreate.getId());

		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();
		//when
		final var response = given()
				.header("Authorization", "Bearer " + token)
				.body(refundTransactionToCreate)
				.accept("application/json")
				.contentType("application/json")
				.when()
				.post("/transactions")
				.then()
				.extract()
				.body()
				.jsonPath()
				.getObject(".", DetailedTransactionDTO.class);

		//then
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.id());

		//Assert Charged Transaction status is refunded
		final var selectedChargeTransactionToAssert = transactionService.getById(chargeTransactionToCreate.getId());
		Assertions.assertEquals(TransactionStatusEnum.REFUNDED, selectedChargeTransactionToAssert.getStatus());

		//Assert Merchant sum is decreased with the full transaction amount
		final var merchantToAssert = (NormalMerchant) merchantService.getById(createdMerchant.getId());
		Assertions.assertEquals(0.0, merchantToAssert.getTotalTransactionSum());
	}

	@Test
	void shouldCreatePartialRefundTransactionSuccessfullyTest() {
		final var createdMerchant = (NormalMerchant) merchantService.createMerchant(defaultMerchant);
		final var authorizeTransaction = Optional.ofNullable(TransactionFactory
						.getTransaction(null,
								createdMerchant,
								100.0,
								TransactionStatusEnum.APPROVED,
								"someCustomer@email.com",
								"+359123123123",
								null,
								TransactionType.AUTHORIZE))
				.map(transactionService::createTransaction).orElseThrow();

		final var chargeTransactionToCreate = Optional.ofNullable(TransactionFactory
						.getTransaction(
								null,
								createdMerchant,
								100.0,
								TransactionStatusEnum.APPROVED,
								"some@email.com",
								"+359123123123",
								authorizeTransaction,
								TransactionType.CHARGE))
				.map(transactionService::createTransaction).orElseThrow();

		final var refundTransactionToCreate = new CreateTransactionDTO(
				TransactionType.REFUND,
				createdMerchant.getId(),
				50.0,
				TransactionStatusEnum.APPROVED,
				"some@email.com",
				"+359123123123",
				chargeTransactionToCreate.getId());

		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();
		//when
		final var response = given()
				.header("Authorization", "Bearer " + token)
				.body(refundTransactionToCreate)
				.accept("application/json")
				.contentType("application/json")
				.when()
				.post("/transactions")
				.then()
				.extract()
				.body()
				.jsonPath()
				.getObject(".", DetailedTransactionDTO.class);

		//then
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.id());

		//Assert Charged Transaction status is refunded
		final var selectedChargeTransactionToAssert = transactionService.getById(chargeTransactionToCreate.getId());
		Assertions.assertEquals(TransactionStatusEnum.REFUNDED, selectedChargeTransactionToAssert.getStatus());

		//Assert Merchant sum is decreased with the full transaction amount
		final var merchantToAssert = (NormalMerchant) merchantService.getById(createdMerchant.getId());
		Assertions.assertEquals(50.0, merchantToAssert.getTotalTransactionSum());
	}

	@Test
	void shouldCreateRefundTransactionWithoutDecreasingMerchantSumTest() {
		final var createdMerchant = (NormalMerchant) merchantService.createMerchant(defaultMerchant);
		final var authorizeTransaction = Optional.ofNullable(TransactionFactory
						.getTransaction(null,
								createdMerchant,
								100.0,
								TransactionStatusEnum.APPROVED,
								"someCustomer@email.com",
								"+359123123123",
								null,
								TransactionType.AUTHORIZE))
				.map(transactionService::createTransaction).orElseThrow();

		final var chargeTransactionToCreate = Optional.ofNullable(TransactionFactory
						.getTransaction(
								null,
								createdMerchant,
								100.0,
								TransactionStatusEnum.APPROVED,
								"some@email.com",
								"+359123123123",
								authorizeTransaction,
								TransactionType.CHARGE))
				.map(transactionService::createTransaction).orElseThrow();

		final var refundTransactionToCreate = new CreateTransactionDTO(
				TransactionType.REFUND,
				createdMerchant.getId(),
				50.0,
				TransactionStatusEnum.ERROR,
				"some@email.com",
				"+359123123123",
				chargeTransactionToCreate.getId());

		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();
		//when
		final var response = given()
				.header("Authorization", "Bearer " + token)
				.body(refundTransactionToCreate)
				.accept("application/json")
				.contentType("application/json")
				.when()
				.post("/transactions")
				.then()
				.extract()
				.body()
				.jsonPath()
				.getObject(".", DetailedTransactionDTO.class);

		//then
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.id());

		//Assert Charged Transaction status is refunded
		final var selectedChargeTransactionToAssert = transactionService.getById(chargeTransactionToCreate.getId());
		Assertions.assertEquals(TransactionStatusEnum.REFUNDED, selectedChargeTransactionToAssert.getStatus());

		//Assert Merchant sum is not decreased due to bad status
		final var merchantToAssert = (NormalMerchant) merchantService.getById(createdMerchant.getId());
		Assertions.assertEquals(100.0, merchantToAssert.getTotalTransactionSum());
	}

	@Test
	void shouldFailCreateNegativeRefundTransactionDueToNegativeAmountTest() {
		final var createdMerchant = (NormalMerchant) merchantService.createMerchant(defaultMerchant);
		final var authorizeTransaction = Optional.ofNullable(TransactionFactory
						.getTransaction(null,
								createdMerchant,
								100.0,
								TransactionStatusEnum.APPROVED,
								"someCustomer@email.com",
								"+359123123123",
								null,
								TransactionType.AUTHORIZE))
				.map(transactionService::createTransaction).orElseThrow();

		final var chargeTransactionToCreate = Optional.ofNullable(TransactionFactory
						.getTransaction(
								null,
								createdMerchant,
								100.0,
								TransactionStatusEnum.APPROVED,
								"some@email.com",
								"+359123123123",
								authorizeTransaction,
								TransactionType.CHARGE))
				.map(transactionService::createTransaction).orElseThrow();

		final var refundTransactionToCreate = new CreateTransactionDTO(
				TransactionType.REFUND,
				createdMerchant.getId(),
				-50.0,
				TransactionStatusEnum.APPROVED,
				"some@email.com",
				"+359123123123",
				chargeTransactionToCreate.getId());

		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();
		//when
		final var response = given()
				.header("Authorization", "Bearer " + token)
				.body(refundTransactionToCreate)
				.accept("application/json")
				.contentType("application/json")
				.when()
				.post("/transactions");

		//then
		Assertions.assertNotNull(response);
		Assertions.assertEquals("The amount must be > 0", response.print());
	}
}
