package com.shukriev.merchantplatform.outbound.merchant;

import com.shukriev.merchantplatform.model.merchant.Merchant;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MerchantProvider {
	@NotNull
	Optional<Merchant> getById(final UUID id);

	@NotNull
	Set<Merchant> getMerchants();

	@NotNull
	Merchant updateMerchant(final Merchant merchant);

	@NotNull
	Merchant createMerchant(final Merchant merchant);

	boolean deleteMerchant(final UUID id);
}
