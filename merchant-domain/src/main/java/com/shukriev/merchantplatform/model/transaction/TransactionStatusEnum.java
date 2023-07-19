package com.shukriev.merchantplatform.model.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionStatusEnum {
	APPROVED("Approved"), REVERSED("Reversed"), REFUNDED("Refunded"), ERROR("Error");

	private final String value;

	TransactionStatusEnum(String value) {
		this.value = value;
	}

	@JsonCreator
	public static TransactionStatusEnum fromValue(String value) {
		for (final var status : TransactionStatusEnum.values()) {
			if (status.value.equalsIgnoreCase(value)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid TransactionStatusEnum value: " + value);
	}
}
