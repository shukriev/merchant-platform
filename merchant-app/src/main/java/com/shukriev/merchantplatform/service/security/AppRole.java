package com.shukriev.merchantplatform.service.security;

import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.merchant.admin.AdminMerchant;

public enum AppRole {
	ADMIN(AdminMerchant.class),
	MERCHANT(NormalMerchant.class);

	private final Class<?> value;

	AppRole(Class<?> value) {
		this.value = value;
	}

	public static AppRole fromValue(final Class<?> value) {
		for (final var role : AppRole.values()) {
			if (role.value.equals(value)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Invalid AppRole value: " + value);
	}
}
