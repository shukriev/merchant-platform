package com.shukriev.merchantplatform.model.merchant.factory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MerchantType {
	NORMAL("Normal"), ADMIN("Admin");
	private final String value;

	MerchantType(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static MerchantType fromValue(String value) {
		for (final var type : MerchantType.values()) {
			if (type.value.equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid MerchantType value: " + value);
	}
}
