package com.shukriev.merchantplatform.controller.authentication.dto;

import com.shukriev.merchantplatform.model.merchant.Merchant;

public record MerchantAuthDetails(String id, String email, String username, String role) {

	public static MerchantAuthDetails of(Merchant merchant) {
		return new MerchantAuthDetails(merchant.getId().toString(),
				merchant.getEmail(), merchant.getName(), merchant.role());
	}
}
