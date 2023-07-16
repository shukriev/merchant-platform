package com.shukriev.merchantplatform.merchant;

import com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest.MerchantData.merchant;
import static io.restassured.RestAssured.given;

class MerchantIntegrationTest extends MerchantPlatformIntegrationTest {
	@Autowired
	private MerchantService merchantService;

	@Test
	void shouldReturnMerchantsSuccessfullyTest() {
		merchantService.createMerchant(merchant);

		//when
		final var response = given()
				.accept("application/json")
				.contentType("application/json")
				.when()
				.get("/merchants")
				.then()
				.extract()
				.body()
				.jsonPath()
				.getList(".", NormalMerchant.class);

		//then
		Assertions.assertEquals(1, response.size());
		Assertions.assertAll("Merchants Are Equal",
				() -> Assertions.assertEquals(response.get(0).getEmail(), merchant.getEmail()),
				() -> Assertions.assertEquals(response.get(0).getName(), merchant.getName()),
				() -> Assertions.assertEquals(response.get(0).getStatus(), merchant.getStatus()),
				() -> Assertions.assertEquals(response.get(0).getDescription(), merchant.getDescription()),
				() -> Assertions.assertEquals(response.get(0).getTotalTransactionSum(), merchant.getTotalTransactionSum())
		);
	}

	@Test
	void shouldReturnMerchantByIdSuccessfullyTest() {
		final var createdMerchant = merchantService.createMerchant(merchant);

		//when
		final var response = given()
				.pathParam("id", createdMerchant.getId())
				.accept("application/json")
				.contentType("application/json")
				.when()
				.get("/merchants/{id}")
				.then()
				.extract()
				.body()
				.jsonPath()
				.getObject(".", NormalMerchant.class);

		//then
		Assertions.assertEquals(createdMerchant, response);
	}

	@Test
	void shouldFailMerchantByIdDueToMissingParamTest() {
		//when
		final var response = given()
				.accept("application/json")
				.contentType("application/json")
				.when()
				.get("/merchants/null");

		//then
		Assertions.assertEquals(400, response.getStatusCode());
		Assertions.assertEquals("[id] parameter is mandatory", response.print());
	}

	@Test
	void shouldReturnUpdatedMerchantSuccessfullyTest() {
		final var createdMerchant = merchantService.createMerchant(merchant);

		final var merchantToBeUpdated = new NormalMerchant(
				createdMerchant.getId(),
				createdMerchant.getEmail(),
				createdMerchant.getName(),
				createdMerchant.getPassword(),
				ActiveInactiveStatusEnum.INACTIVE, //Updated to Inactive
				createdMerchant.getDescription(),
				createdMerchant.getTotalTransactionSum());
		//when
		final var response = given()
				.pathParam("id", createdMerchant.getId())
				.body(merchantToBeUpdated)
				.accept("application/json")
				.contentType("application/json")
				.when()
				.put("/merchants/{id}")
				.then()
				.extract()
				.body()
				.jsonPath()
				.getObject(".", NormalMerchant.class);

		//then
		Assertions.assertEquals(merchantToBeUpdated, response);
	}

	@Test
	void shouldFailUpdatedMerchantDueToNonMatchingTest() {
		final var createdMerchant = merchantService.createMerchant(merchant);

		final var merchantToBeUpdated = new NormalMerchant(
				UUID.randomUUID(),
				createdMerchant.getEmail(),
				createdMerchant.getName(),
				createdMerchant.getPassword(),
				ActiveInactiveStatusEnum.INACTIVE, //Updated to Inactive
				createdMerchant.getDescription(),
				createdMerchant.getTotalTransactionSum());
		//when
		final var response = given()
				.pathParam("id", createdMerchant.getId())
				.body(merchantToBeUpdated)
				.accept("application/json")
				.contentType("application/json")
				.when()
				.put("/merchants/{id}");

		//then
		Assertions.assertEquals(400, response.getStatusCode());
		Assertions.assertEquals(
				"The id parameter is mandatory and should be matching to payload.id",
				response.print());
	}


	@Test
	void shouldDeleteMerchantSuccessfullyTest() {
		final var createdMerchant = merchantService.createMerchant(merchant);

		//when
		final var response = given()
				.pathParam("id", createdMerchant.getId())
				.accept("application/json")
				.contentType("application/json")
				.when()
				.delete("/merchants/{id}");

		//then
		Assertions.assertEquals(200, response.getStatusCode());
	}

	// TODO Implement some more bad test case scenarios
	// Failure during createTransaction due to inactive merchant
}
