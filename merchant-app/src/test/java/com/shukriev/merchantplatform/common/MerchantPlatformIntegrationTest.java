package com.shukriev.merchantplatform.common;

import com.shukriev.merchantplatform.MerchantPlatformApplication;
import com.shukriev.merchantplatform.controller.transaction.dto.CreateTransactionDTO;
import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.transaction.TransactionStatusEnum;
import com.shukriev.merchantplatform.model.transaction.factory.TransactionType;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MerchantPlatformApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MerchantPlatformIntegrationTest {

	@LocalServerPort
	int port;

	@BeforeEach
	public void setUp() {
		RestAssured.port = port;
	}

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

	public static class MerchantData {
		public static final NormalMerchant merchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0
		);

		public static final NormalMerchant defaultMerchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				0.0
		);
	}

	public static class TransactionData {
		public static CreateTransactionDTO getCreateTransactionDTO(final TransactionType transactionType, final NormalMerchant merchant) {
			return new CreateTransactionDTO(
					transactionType,
					merchant.getId(),
					10.0,
					TransactionStatusEnum.APPROVED,
					"some@email.com",
					"+359123123123",
					null);
		}
	}
}
