package com.shukriev.merchantplatform.inbound.merchant;

import com.shukriev.merchantplatform.exception.merchant.MerchantNotFoundException;
import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.outbound.merchant.MerchantProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {
	@Mock
	private MerchantProvider merchantProvider;

	private MerchantService merchantService;

	@BeforeEach
	public void init() {
		merchantService = new MerchantServiceImpl(merchantProvider);
	}

	@Test
	void shouldReturnValidMerchantsTest() {
		final var merchant = new NormalMerchant(
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d);

		// given
		final var merchants = Set.of(merchant);
		when(merchantProvider.getMerchants()).thenReturn(merchants);
		// when
		final var result = merchantService.getMerchants();
		// then
		Assertions.assertEquals(merchants, result);
	}

	@Test
	void shouldThrowNotFoundForGetMerchantsTest() {
		// given
		when(merchantProvider.getMerchants()).thenReturn(Set.of());
		// when -> then
		Assertions.assertThrows(MerchantNotFoundException.class, () -> merchantService.getMerchants());
	}

	@Test
	void shouldReturnValidMerchantWhenGetByIdIsPerformedTest() {
		final var generatedUuid = UUID.randomUUID();

		final var merchant = new NormalMerchant(
				generatedUuid,
				"some@mail.com",
				"some_name",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d);

		// given
		when(merchantProvider.getById(generatedUuid)).thenReturn(Optional.of(merchant));
		// when
		final NormalMerchant result = merchantService.getById(generatedUuid);
		// then
		Assertions.assertEquals(merchant, result);
	}

	@Test
	void shouldUpdateMerchantSuccessfullyTest() {
		final var merchant = new NormalMerchant(
				"some@mail.com",
				"some_name_updated",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d);
		// given
		when(merchantProvider.updateMerchant(merchant)).thenReturn(merchant);
		// when
		final var updatedMerchant = merchantService.updateMerchant(merchant);
		Assertions.assertEquals(merchant, updatedMerchant);
	}

	@Test
	void shouldCreateMerchantSuccessfullyTest() {
		final var merchant = new NormalMerchant(
				"some@mail.com",
				"some_name_updated",
				"some_password",
				ActiveInactiveStatusEnum.ACTIVE,
				"some_description",
				1.0d);
		// given
		when(merchantProvider.createMerchant(merchant)).thenReturn(merchant);
		// when
		final var updatedMerchant = merchantService.createMerchant(merchant);
		Assertions.assertEquals(merchant, updatedMerchant);
	}

	@Test
	void shouldDeleteMerchantSuccessfullyTest() {
		// given
		final var uuid = UUID.randomUUID();
		when(merchantProvider.deleteMerchant(uuid)).thenReturn(true);
		// when
		final var isMerchantDeleted = merchantService.deleteMerchant(uuid);
		Assertions.assertTrue(isMerchantDeleted);
	}
}
