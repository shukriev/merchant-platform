package com.shukriev.merchantplatform.rake;

import com.shukriev.merchantplatform.common.MerchantPlatformIntegrationTest;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.merchant.admin.AdminMerchant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

class MerchantInitializerTest extends MerchantPlatformIntegrationTest {
	@Container
	private static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:11.1")
			.withDatabaseName("test")
			.withUsername("sa")
			.withPassword("sa");

	@AfterAll
	static void afterAll() {
		postgresqlContainer.stop();
	}
	@DynamicPropertySource
	private static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgresqlContainer::getUsername);
		registry.add("spring.datasource.password", postgresqlContainer::getPassword);
	}
	
	@Autowired
	private MerchantInitializer merchantInitializer;
	@Autowired
	private MerchantService merchantService;

	@Test
	void shouldInitializeMerchantsSuccessfully() {

		merchantInitializer.run("src/test/resources/merchantData/merchantData.csv");

		final var merchants = merchantService.getMerchants();
		Assertions.assertEquals(2, merchants.stream().filter(m -> AdminMerchant.class.equals(m.getClass())).count());
		Assertions.assertEquals(8, merchants.stream().filter(m -> NormalMerchant.class.equals(m.getClass())).count());
	}
}
