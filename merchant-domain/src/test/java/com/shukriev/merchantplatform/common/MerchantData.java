package com.shukriev.merchantplatform.common;

import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;

public final class MerchantData {
	private MerchantData() {
	}

	public static final NormalMerchant merchant = new NormalMerchant(
			"some@mail.com",
			"some_name",
			"some_password",
			ActiveInactiveStatusEnum.ACTIVE,
			"some_description",
			1.0
	);

	public static final NormalMerchant inactiveMerchant = new NormalMerchant(
			"inactiveMerchant@mail.com",
			"inactive_merchant",
			"some_password",
			ActiveInactiveStatusEnum.INACTIVE,
			"some_description",
			1.0
	);
}