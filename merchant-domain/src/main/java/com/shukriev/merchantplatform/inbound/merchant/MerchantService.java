package com.shukriev.merchantplatform.inbound.merchant;

import com.shukriev.merchantplatform.model.merchant.NormalMerchant;

import java.util.Set;
import java.util.UUID;

public interface MerchantService {
	NormalMerchant getById(final UUID id);

	Set<NormalMerchant> getMerchants();

	NormalMerchant updateMerchant(final NormalMerchant merchant);
}
