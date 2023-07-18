package com.shukriev.merchantplatform.model.merchant.factory;

import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.Merchant;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.merchant.admin.AdminMerchant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.UUID;

class MerchantFactoryTest {
	private static List<Map.Entry<Class<?>, Merchant>> validMerchantMap() {
		return List.of(
				Map.entry(NormalMerchant.class, MerchantFactory.getMerchant(
						UUID.randomUUID(),
						"normalMerchant@email.com",
						"normalMerchant",
						"somePassword",
						ActiveInactiveStatusEnum.ACTIVE,
						"Some normal merchant description",
						100.0,
						MerchantType.NORMAL)),
				Map.entry(AdminMerchant.class, MerchantFactory.getMerchant(
						UUID.randomUUID(),
						"adminMerchant@email.com",
						"adminMerchant",
						"somePassword",
						ActiveInactiveStatusEnum.ACTIVE,
						null,
						null,
						MerchantType.ADMIN))
		);
	}

	@ParameterizedTest
	@MethodSource("validMerchantMap")
	void shouldReturnMerchantSuccessfully(Map.Entry<Class<?>, Merchant> entry) {
		Assertions.assertEquals(entry.getKey(), entry.getValue().getClass());
		Assertions.assertNotNull(entry.getValue().getId());

		if (NormalMerchant.class.equals(entry.getKey())) {
			final var data = ((NormalMerchant) entry.getValue());
			Assertions.assertEquals("normalMerchant@email.com", data.getEmail());
			Assertions.assertEquals("normalMerchant", data.getName());
			Assertions.assertEquals("somePassword", data.getPassword());
			Assertions.assertEquals(ActiveInactiveStatusEnum.ACTIVE, data.getStatus());
			Assertions.assertEquals("Some normal merchant description", data.getDescription());
			Assertions.assertEquals(100.0, data.getTotalTransactionSum());
		} else if (AdminMerchant.class.equals(entry.getKey())) {
			final var data = ((AdminMerchant) entry.getValue());
			Assertions.assertEquals("adminMerchant@email.com", data.getEmail());
			Assertions.assertEquals("adminMerchant", data.getName());
			Assertions.assertEquals("somePassword", data.getPassword());
			Assertions.assertEquals(ActiveInactiveStatusEnum.ACTIVE, data.getStatus());
		} else {
			throw new IllegalArgumentException("Merchant Type is not implemented in test");
		}
	}
}
