package com.shukriev.merchantplatform.model.merchant.factory;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MerchantType {
	NORMAL("Normal"), ADMIN("Admin");
	private final String value;

	MerchantType(String value) {
		this.value = value;
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
