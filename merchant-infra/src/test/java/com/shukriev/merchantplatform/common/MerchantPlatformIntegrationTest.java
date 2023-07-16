package com.shukriev.merchantplatform.common;

import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.transaction.AuthorizeTransaction;
import com.shukriev.merchantplatform.model.transaction.Transaction;
import com.shukriev.merchantplatform.model.transaction.TransactionStatusEnum;
import org.junit.jupiter.api.AfterEach;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

@Testcontainers
public class MerchantPlatformIntegrationTest {
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

	@AfterEach
	void afterEach() throws SQLException {
		final var jdbcUrl = postgresqlContainer.getJdbcUrl();
		final var username = postgresqlContainer.getUsername();
		final var password = postgresqlContainer.getPassword();

		try (final var connection = DriverManager.getConnection(jdbcUrl, username, password);
			 final var statement = connection.createStatement()) {
			final var tablesToTruncate = List.of("merchant", "transaction");
			tablesToTruncate
					.forEach(tableName -> {
						try {
							statement.executeUpdate(MessageFormat.format("TRUNCATE TABLE {0} CASCADE", tableName));
						} catch (SQLException e) {
							throw new RuntimeException(e);
						}
					});
		} catch (SQLException e) {
			// Handle any exceptions that occur during cleanup
			e.printStackTrace();
		}
	}

	public static class MerchantData {
		public static final NormalMerchant merchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d
		);
	}

	public static class TransactionData {
		public static Transaction of(final NormalMerchant merchant) {
			return new AuthorizeTransaction(
					null, // Uuid
					merchant, // Merchant
					10.0, // Amount
					TransactionStatusEnum.APPROVED, // Status
					"some@email.com", // CustomerEmail
					"+359123123123", // CustomerPhone
					null);
		}
	}
}
