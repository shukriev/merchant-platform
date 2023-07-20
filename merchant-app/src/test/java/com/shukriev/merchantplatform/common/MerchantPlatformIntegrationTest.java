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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MerchantPlatformApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MerchantPlatformIntegrationTest {
	@LocalServerPort
	int port;

	protected void cleanDatabase(PostgreSQLContainer<?> postgresqlContainer) {
		final var jdbcUrl = postgresqlContainer.getJdbcUrl();
		final var username = postgresqlContainer.getUsername();
		final var password = postgresqlContainer.getPassword();

		try (final var connection = DriverManager.getConnection(jdbcUrl, username, password);
			 final var statement = connection.createStatement()) {
			final var tablesToTruncate = List.of("merchant", "transaction");
			tablesToTruncate
					.forEach(tableName -> {
						try {
							final var checkIfExistsQuery = MessageFormat.format("SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ''{0}''", tableName);
							final var resultSet = statement.executeQuery(checkIfExistsQuery);
							if (resultSet.next()) {
								final var truncateTableQuery = MessageFormat.format("TRUNCATE TABLE {0} CASCADE", tableName);
								statement.execute(truncateTableQuery);
							}
						} catch (SQLException e) {
							throw new RuntimeException(e);
						}
					});
		} catch (SQLException e) {
			// Handle any exceptions that occur during cleanup
			e.printStackTrace();
		}
	}

	@BeforeEach
	public void setUp() {
		RestAssured.port = port;
	}

	public static class MerchantData {
		public static final NormalMerchant merchant = new NormalMerchant(
				"Flo_Prosacco6@yahoo.com",
				"some_name",
				"$2a$12$9WTyzcN3vlOb6vb26tE24eGPlI4b66PS/BrKxyJ8aSjWF7ma7gyHq", //Decoded password 12345
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0
		);

		public static final NormalMerchant defaultMerchant = new NormalMerchant(
				"Flo_Prosacco6@yahoo.com",
				"some_name",
				"$2a$12$9WTyzcN3vlOb6vb26tE24eGPlI4b66PS/BrKxyJ8aSjWF7ma7gyHq", //Decoded password 12345
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
					"Flo_Prosacco6@yahoo.com",
					"+359123123123",
					null);
		}
	}
}
