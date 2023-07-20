package com.shukriev.merchantplatform.merchant;

import com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest;
import com.shukriev.merchantplatform.controller.authentication.dto.SignInRequest;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.service.security.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.UUID;

import static com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest.MerchantData.merchant;
import static io.restassured.RestAssured.given;

class MerchantIntegrationTest extends MerchantPlatformIntegrationTest {

	@Autowired
	private AuthenticationService authenticationService;

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

	@Test
	void shouldReturnMerchantsSuccessfullyTest() {
		final var createdMerchant = (NormalMerchant) merchantService.createMerchant(merchant);

		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();
		//when
		final var response = given()
				.accept("application/json")
				.contentType("application/json")
				.header("Authorization", "Bearer " + token)
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
				() -> Assertions.assertEquals(createdMerchant.getId(), response.get(0).getId()),
				() -> Assertions.assertEquals(createdMerchant.getEmail(), response.get(0).getEmail()),
				() -> Assertions.assertEquals(createdMerchant.getName(), response.get(0).getName()),
				() -> Assertions.assertEquals(createdMerchant.getStatus(), response.get(0).getStatus()),
				() -> Assertions.assertEquals(createdMerchant.getDescription(), response.get(0).getDescription()),
				() -> Assertions.assertEquals(createdMerchant.getTotalTransactionSum(), response.get(0).getTotalTransactionSum())
		);
	}

	@Test
	void shouldReturnMerchantByIdSuccessfullyTest() {
		final var createdMerchant = merchantService.createMerchant(merchant);
		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();

		//when
		final var response = given()
				.pathParam("id", createdMerchant.getId())
				.accept("application/json")
				.contentType("application/json")
				.header("Authorization", "Bearer " + token)
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
		final var createdMerchant = merchantService.createMerchant(merchant);
		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();

		//when
		final var response = given()
				.accept("application/json")
				.contentType("application/json")
				.header("Authorization", "Bearer " + token)
				.when()
				.get("/merchants/null");

		//then
		Assertions.assertEquals(400, response.getStatusCode());
		Assertions.assertEquals("[id] parameter is mandatory", response.print());
	}

	@Test
	void shouldReturnUpdatedMerchantSuccessfullyTest() {
		final var createdMerchant = (NormalMerchant) merchantService.createMerchant(merchant);
		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();

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
				.header("Authorization", "Bearer " + token)
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
		final var createdMerchant = (NormalMerchant) merchantService.createMerchant(merchant);
		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();

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
				.header("Authorization", "Bearer " + token)
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
		final var token = authenticationService.signIn(
				new SignInRequest(createdMerchant.getEmail(), "12345")).token();

		//when
		final var response = given()
				.pathParam("id", createdMerchant.getId())
				.header("Authorization", "Bearer " + token)
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
