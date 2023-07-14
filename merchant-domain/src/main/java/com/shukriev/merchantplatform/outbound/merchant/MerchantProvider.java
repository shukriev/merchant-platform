package com.shukriev.merchantplatform.outbound.merchant;

import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MerchantProvider {
	@NotNull
	Optional<NormalMerchant> getById(final UUID id);

	@NotNull
	Set<NormalMerchant> getMerchants();

	@NotNull
	NormalMerchant updateMerchant(final NormalMerchant merchant);

	@NotNull
	NormalMerchant createMerchant(final NormalMerchant merchant);
}
