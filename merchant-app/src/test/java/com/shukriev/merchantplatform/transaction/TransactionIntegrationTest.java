package com.shukriev.merchantplatform.transaction;

import com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest;
import com.shukriev.merchantplatform.controller.transaction.dto.DetailedTransactionDTO;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.model.transaction.factory.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest.MerchantData.merchant;
import static io.restassured.RestAssured.given;

class TransactionIntegrationTest extends MerchantPlatformIntegrationTest {
	@Autowired
	private MerchantService merchantService;

	@Test
	void shouldCreateTransactionSuccessfullyTest() {
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
}
