package com.shukriev.merchantplatform.model.transaction;

import com.shukriev.merchantplatform.common.MerchantData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

class TransactionModelTest {
	private Validator validator;

	@BeforeEach
	public void setUp() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	private static List<Transaction> validTransactionList() {
		return List.of(
				new AuthorizeTransaction(
						null, // Uuid
						MerchantData.merchant, // Merchant
						10.0, // Amount
						TransactionStatusEnum.APPROVED, // Status
						"some@email.com", // CustomerEmail
						"+359123123123", // CustomerPhone
						null), // TransactionReference
				new ChargeTransaction(
						null,
						MerchantData.merchant,
						10.0,
						TransactionStatusEnum.APPROVED,
						"some@email.com",
						"+359123123123",
						null),
				new RefundTransaction(
						null,
						MerchantData.merchant,
						10.0,
						TransactionStatusEnum.APPROVED,
						"some@email.com",
						"+359123123123",
						null),
				new ReversalTransaction(
						null,
						MerchantData.merchant,
						10.0,
						TransactionStatusEnum.APPROVED,
						"some@email.com",
						"+359123123123",
						null)
		);
	}

	@ParameterizedTest
	@MethodSource("validTransactionList")
	void shouldBeValidTransactionTest(Transaction transaction) {
		// when
		final Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
		// then
		Assertions.assertTrue(violations.isEmpty());
	}

	@Test
	void shouldBeInvalidTransactionTest() {
		// given
		final var transaction = new AuthorizeTransaction(
				null,
				MerchantData.merchant,
				0.0, // Bad Amount
				TransactionStatusEnum.APPROVED,
				"someBadEmail", // Bad Email
				"+30123123123", // Bad Phone Number
				null);
		// when
		final List<ConstraintViolation<AuthorizeTransaction>> violations = validator.validate(transaction).stream()
				.sorted(Comparator.comparing(ConstraintViolation::getMessage)).toList();
		// then
		Assertions.assertEquals(3, violations.size());
		Assertions.assertEquals("Invalid customer email address", violations.get(0).getMessage());
		Assertions.assertEquals("The amount must be > 0", violations.get(1).getMessage());
		Assertions.assertEquals("Wrong country code provided. It has to be +359 or starting with 0", violations.get(2).getMessage());
	}


	@Test
	void shouldBeInvalidTransactionDueToNullMerchantTest() {
		// given
		final var transaction = new AuthorizeTransaction(
				null,
				null, // Bad Merchant
				10.0,
				TransactionStatusEnum.APPROVED,
				"email@email.com",
				"+359123123123",
				null);
		// when
		final List<ConstraintViolation<AuthorizeTransaction>> violations = validator.validate(transaction).stream().toList();
		// then
		Assertions.assertEquals(1, violations.size());
		Assertions.assertEquals("Transaction Merchant is required", violations.get(0).getMessage());
	}
}
