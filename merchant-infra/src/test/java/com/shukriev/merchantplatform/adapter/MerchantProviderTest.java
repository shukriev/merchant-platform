package com.shukriev.merchantplatform.adapter;

import com.shukriev.merchantplatform.MerchantInfraMain;
import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = MerchantInfraMain.class)
class MerchantProviderTest {
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

	@Autowired
	private MerchantProviderImpl merchantProvider;

	@Test
	void shouldCreateMerchantSuccessfullyTest() {
		final var merchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d
		);

		final var createdMerchant = merchantProvider.createMerchant(merchant);

		Assertions.assertNotNull(createdMerchant);
		//Validate that the UUID is generated
		Assertions.assertNotNull(createdMerchant.getId());
	}

	@Test
	void shouldUpdateMerchantSuccessfullyTest() {
		final var merchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d
		);

		final var createdMerchant = merchantProvider.createMerchant(merchant);
		final var updateMerchant = new NormalMerchant(
				createdMerchant.getId(),
				createdMerchant.getEmail(),
				createdMerchant.getName(),
				createdMerchant.getPassword(),
				ActiveInactiveStatusEnum.INACTIVE,
				createdMerchant.getDescription(),
				createdMerchant.getTotalTransactionSum());
		final var updatedMerchant = merchantProvider.updateMerchant(updateMerchant);

		Assertions.assertNotNull(updatedMerchant);

		//Validate that the UUID is not changed
		Assertions.assertEquals(merchant.getId(), updatedMerchant.getId());
		Assertions.assertEquals(ActiveInactiveStatusEnum.INACTIVE, updatedMerchant.getStatus());
	}


	@Test
	void shouldGetMerchantByIdSuccessfullyTest() {
		final var merchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d
		);

		final var createdMerchant = merchantProvider.createMerchant(merchant);
		final var selectedMerchant = merchantProvider.getById(createdMerchant.getId());

		Assertions.assertNotNull(selectedMerchant);
		Assertions.assertTrue(selectedMerchant.isPresent());
		Assertions.assertEquals(createdMerchant, selectedMerchant.get());
	}

	@Test
	void shouldReturnAllMerchantsSuccessfullyTest() {
		final var merchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d
		);

		final var secondMerchant = new NormalMerchant(
				"some2@mail.com",
				"some_name2",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description2",
				1.0d
		);

		merchantProvider.createMerchant(merchant);
		merchantProvider.createMerchant(secondMerchant);

		final var selectedMerchants = merchantProvider.getMerchants();
		Assertions.assertNotNull(selectedMerchants);
		Assertions.assertEquals(2, selectedMerchants.size());
	}

	//TODO implement some more tests
}
