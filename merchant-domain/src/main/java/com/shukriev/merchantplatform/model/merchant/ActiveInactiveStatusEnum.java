package com.shukriev.merchantplatform.model.merchant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActiveInactiveStatusEnum {
	ACTIVE("Active"), INACTIVE("Inactive");
	private final String value;

	ActiveInactiveStatusEnum(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static ActiveInactiveStatusEnum fromValue(String value) {
		for (final var status : ActiveInactiveStatusEnum.values()) {
			if (status.value.equalsIgnoreCase(value)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid ActiveInactiveStatus value: " + value);
	}
}
