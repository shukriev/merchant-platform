package com.shukriev.merchantplatform.model.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionStatusEnum {
	APPROVED("Approved"), REVERSED("Reversed"), REFUNDED("Refunded"), ERROR("Error");

	private final String value;

	TransactionStatusEnum(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
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
