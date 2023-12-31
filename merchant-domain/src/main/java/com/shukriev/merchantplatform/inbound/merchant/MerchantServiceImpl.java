package com.shukriev.merchantplatform.inbound.merchant;

import com.shukriev.merchantplatform.exception.merchant.MerchantNotFoundException;
import com.shukriev.merchantplatform.model.merchant.Merchant;
import com.shukriev.merchantplatform.outbound.merchant.MerchantProvider;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class MerchantServiceImpl implements MerchantService {
	private final MerchantProvider merchantProvider;

	public MerchantServiceImpl(final MerchantProvider merchantProvider) {
		this.merchantProvider = merchantProvider;
	}

	@Override
	public Merchant getById(final UUID id) {
		final var merchant = merchantProvider.getById(id);
		return merchant.orElseThrow(() ->
				new MerchantNotFoundException(MessageFormat.format("Merchant with id: {0} not found", id)));
	}

	@Override
	public Optional<Merchant> getByEmail(final String email) {
		return merchantProvider.getByEmail(email);
	}

	@Override
	public Set<Merchant> getMerchants() {
		final var merchants = merchantProvider.getMerchants();
		if (merchants.isEmpty()) {
			throw new MerchantNotFoundException("Merchants not found");
		}

		return merchants;
	}

	@Override
	public Merchant updateMerchant(final Merchant merchant) {
		return merchantProvider.updateMerchant(merchant);
	}

	@Override
	public Merchant createMerchant(final Merchant merchant) {
		return merchantProvider.createMerchant(merchant);
	}

	@Override
	public boolean deleteMerchant(final UUID id) {
		//TODO Ensure you prevent a merchant from being deleted unless there are no related payment transactions
		return merchantProvider.deleteMerchant(id);
	}
}
