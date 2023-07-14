package com.shukriev.merchantplatform.adapter;

import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.outbound.merchant.MerchantProvider;
import com.shukriev.merchantplatform.repository.postgres.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MerchantProviderImpl implements MerchantProvider {
	private final MerchantRepository merchantRepository;

	@Autowired
	public MerchantProviderImpl(final MerchantRepository merchantRepository) {
		this.merchantRepository = merchantRepository;
	}

	@Override
	public Optional<NormalMerchant> getById(UUID id) {
		return merchantRepository.findById(id);
	}

	@Override
	public Set<NormalMerchant> getMerchants() {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(merchantRepository.findAll().iterator(), Spliterator.ORDERED),
				false).collect(Collectors.toSet());
	}

	@Override
	public NormalMerchant updateMerchant(NormalMerchant merchant) {
		return merchantRepository.save(merchant);
	}

	@Override
	public NormalMerchant createMerchant(NormalMerchant merchant) {
		return merchantRepository.save(merchant);
	}

	@Override
	public boolean deleteMerchant(UUID id) {
		try {
			merchantRepository.deleteById(id);
		} catch (final Exception e) {
			//TODO Do something with the exception, at least we can log it
			return false;
		}
		return true;
	}
}
