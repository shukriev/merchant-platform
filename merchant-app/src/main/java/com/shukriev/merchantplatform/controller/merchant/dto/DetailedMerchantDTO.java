package com.shukriev.merchantplatform.controller.merchant.dto;

import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;

import java.util.UUID;

public record DetailedMerchantDTO(UUID id, String email, String name, ActiveInactiveStatusEnum status,
								  String description, Double totalTransactionSum) {
	public static DetailedMerchantDTO of(NormalMerchant merchant) {
		return new DetailedMerchantDTO(
				merchant.getId(),
				merchant.getEmail(),
				merchant.getName(),
				merchant.getStatus(),
				merchant.getDescription(),
				merchant.getTotalTransactionSum());
	}
}
