package com.shukriev.merchantplatform.model.merchant.factory;

import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.Merchant;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.merchant.admin.AdminMerchant;

import java.util.UUID;

public final class MerchantFactory {
	private MerchantFactory() {
	}

	public static Merchant getMerchant(
			final UUID id,
			final String email,
			final String name,
			final String password,
			final ActiveInactiveStatusEnum status,
			final String description,
			final Double totalTransactionSum,
			final MerchantType merchantType) {
		return switch (merchantType) {
			case NORMAL -> new NormalMerchant(
					id,
					email,
					name,
					password,
					status,
					description,
					totalTransactionSum);
			case ADMIN -> new AdminMerchant(
					id,
					email,
					name,
					password,
					status);
		};
	}
}
