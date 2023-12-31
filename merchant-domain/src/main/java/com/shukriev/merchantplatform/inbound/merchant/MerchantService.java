package com.shukriev.merchantplatform.inbound.merchant;

import com.shukriev.merchantplatform.model.merchant.Merchant;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MerchantService {
	Merchant getById(final UUID id);

	Optional<Merchant> getByEmail(final String email);

	Set<Merchant> getMerchants();

	Merchant updateMerchant(final Merchant merchant);

	Merchant createMerchant(final Merchant merchant);

	boolean deleteMerchant(final UUID id);
}
