package com.shukriev.merchantplatform.model.transaction.factory;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionType {
	AUTHORIZE("Authorize"), CHARGE("Charge"), REFUND("Refund"), REVERSAL("Reversal");

	private final String value;

	TransactionType(String value) {
		this.value = value;
	}
	
	@JsonCreator
	public static TransactionType fromValue(String value) {
		for (final var type : TransactionType.values()) {
			if (type.value.equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid TransactionType value: " + value);
	}
}
