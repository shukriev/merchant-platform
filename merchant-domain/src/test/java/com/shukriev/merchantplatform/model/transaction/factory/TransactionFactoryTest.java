package com.shukriev.merchantplatform.model.transaction.factory;

import com.shukriev.merchantplatform.model.transaction.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;

class TransactionFactoryTest {
	private static List<Map.Entry<Class<?>, Transaction>> validTransactionMap() {
		return List.of(
				Map.entry(AuthorizeTransaction.class, TransactionFactory.getTransaction(
						null,
						null,
						10.0,
						TransactionStatusEnum.APPROVED,
						"some@email.com",
						"+359123123123",
						null,
						TransactionType.AUTHORIZE)),
				Map.entry(ChargeTransaction.class, TransactionFactory.getTransaction(
						null,
						null,
						10.0,
						TransactionStatusEnum.APPROVED,
						"some@email.com",
						"+359123123123",
						null,
						TransactionType.CHARGE)),
				Map.entry(RefundTransaction.class, TransactionFactory.getTransaction(
						null,
						null,
						10.0,
						TransactionStatusEnum.REFUNDED,
						"some@email.com",
						"+359123123123",
						null,
						TransactionType.REFUND)),
				Map.entry(ReversalTransaction.class, TransactionFactory.getTransaction(
						null,
						null,
						10.0,
						TransactionStatusEnum.REVERSED,
						"some@email.com",
						"+359123123123",
						null,
						TransactionType.REVERSAL))
		);
	}

	@ParameterizedTest
	@MethodSource("validTransactionMap")
	void shouldReturnTransactionSuccessfully(Map.Entry<Class<?>, Transaction> entry) {
		Assertions.assertEquals(entry.getKey(), entry.getValue().getClass());
		//id = null
		Assertions.assertNull(entry.getValue().getId());
		//merchant = null
		Assertions.assertNull(entry.getValue().getMerchant());
		//Amount = 10.0
		Assertions.assertEquals(10.0, entry.getValue().getAmount());
		//Email = some@email.com
		Assertions.assertEquals("some@email.com", entry.getValue().getCustomerEmail());
		//Phone = +359123123123
		Assertions.assertEquals("+359123123123", entry.getValue().getCustomerPhone());
	}
}
