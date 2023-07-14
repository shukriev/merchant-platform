package com.shukriev.merchantplatform;

import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MerchantPlatformApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MerchantIntegrationTest {
	@LocalServerPort
	int port;

	@BeforeEach
	public void setUp() {
		RestAssured.port = port;
	}

	@Autowired
	private MerchantService merchantService;

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

	@Test
	void shouldReturnMerchantsSuccessfullyTest() {
		//TODO extract to common together with the PG Container Configuration
		final var merchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d
		);

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
		//TODO extract to common together with the PG Container Configuration
		final var merchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d
		);

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
		//TODO extract to common together with the PG Container Configuration
		final var merchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d
		);

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
		//TODO extract to common together with the PG Container Configuration
		final var merchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d
		);

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
		//TODO extract to common together with the PG Container Configuration
		final var merchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d
		);

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

	//TODO Implement some more bad test case scenarios
}
