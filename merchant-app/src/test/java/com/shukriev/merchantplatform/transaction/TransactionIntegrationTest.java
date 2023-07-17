package com.shukriev.merchantplatform.transaction;

import com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest;
import com.shukriev.merchantplatform.controller.transaction.dto.CreateTransactionDTO;
import com.shukriev.merchantplatform.controller.transaction.dto.DetailedTransactionDTO;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.inbound.transaction.TransactionService;
import com.shukriev.merchantplatform.model.transaction.TransactionStatusEnum;
import com.shukriev.merchantplatform.model.transaction.factory.TransactionFactory;
import com.shukriev.merchantplatform.model.transaction.factory.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest.MerchantData.defaultMerchant;
import static com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest.MerchantData.merchant;
import static io.restassured.RestAssured.given;

class TransactionIntegrationTest extends MerchantPlatformIntegrationTest {
	@Autowired
	private MerchantService merchantService;

	@Autowired
	private TransactionService transactionService;

	@Test
	void shouldCreateAuthorizeTransactionSuccessfullyTest() {
		final var createdMerchant = merchantService.createMerchant(merchant);
		final var transactionToCreate = TransactionData.getCreateTransactionDTO(TransactionType.AUTHORIZE, createdMerchant);

		//when
		final var response = given()
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
		final var createdMerchant = merchantService.createMerchant(defaultMerchant);
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

		//when
		final var response = given()
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
		final var selectedMerchantToAssert = merchantService.getById(createdMerchant.getId());
		Assertions.assertEquals(100.0, selectedMerchantToAssert.getTotalTransactionSum());
	}

	@Test
	void shouldCreateChargeTransactionWithoutUpdatingMerchantSumSuccessfullyTest() {
		final var createdMerchant = merchantService.createMerchant(defaultMerchant);
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

		//when
		final var response = given()
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
		final var selectedMerchantToAssert = merchantService.getById(createdMerchant.getId());
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

		//when
		final var response = given()
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
		final var createdMerchant = merchantService.createMerchant(defaultMerchant);
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

		//when
		final var response = given()
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

		//when
		final var response = given()
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
		final var createdMerchant = merchantService.createMerchant(defaultMerchant);
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

		//when
		final var response = given()
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
		final var merchantToAssert = merchantService.getById(createdMerchant.getId());
		Assertions.assertEquals(0.0, merchantToAssert.getTotalTransactionSum());
	}

	@Test
	void shouldCreatePartialRefundTransactionSuccessfullyTest() {
		final var createdMerchant = merchantService.createMerchant(defaultMerchant);
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

		//when
		final var response = given()
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
		final var merchantToAssert = merchantService.getById(createdMerchant.getId());
		Assertions.assertEquals(50.0, merchantToAssert.getTotalTransactionSum());
	}

	@Test
	void shouldCreateRefundTransactionWithoutDecreasingMerchantSumTest() {
		final var createdMerchant = merchantService.createMerchant(defaultMerchant);
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

		//when
		final var response = given()
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
		final var merchantToAssert = merchantService.getById(createdMerchant.getId());
		Assertions.assertEquals(100.0, merchantToAssert.getTotalTransactionSum());
	}

	@Test
	void shouldFailCreateNegativeRefundTransactionDueToNegativeAmountTest() {
		final var createdMerchant = merchantService.createMerchant(defaultMerchant);
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

		//when
		final var response = given()
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
