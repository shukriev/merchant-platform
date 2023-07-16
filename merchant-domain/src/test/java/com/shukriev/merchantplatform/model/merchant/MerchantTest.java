package com.shukriev.merchantplatform.model.merchant;

import com.shukriev.merchantplatform.common.MerchantData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

class MerchantTest {
	private Validator validator;

	@BeforeEach
	public void setUp() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	@Test
	void shouldBeValidMerchantTest() {
		// given
		final NormalMerchant merchant = MerchantData.merchant;
		// when
		final Set<ConstraintViolation<NormalMerchant>> violations = validator.validate(merchant);
		// then
		Assertions.assertTrue(violations.isEmpty());
	}

	@Test
	void shouldFailDueToBadEmailTest() {
		// given
		final NormalMerchant merchant = MerchantData.merchant;
		// when
		final Set<ConstraintViolation<NormalMerchant>> violations = validator.validate(merchant);
		// then
		Assertions.assertFalse(violations.isEmpty());
		Assertions.assertEquals("must be a well-formed email address", violations.stream().findFirst().get().getMessage());
	}

	@Test
	void shouldFailDueToBadTotalTransactionSumTest() {
		// given
		final NormalMerchant merchant = MerchantData.merchant;

		// when
		final Set<ConstraintViolation<NormalMerchant>> violations = validator.validate(merchant);
		// then
		Assertions.assertFalse(violations.isEmpty());
		Assertions.assertEquals("must be greater than or equal to 0", violations.stream().findFirst().get().getMessage());
	}
}
