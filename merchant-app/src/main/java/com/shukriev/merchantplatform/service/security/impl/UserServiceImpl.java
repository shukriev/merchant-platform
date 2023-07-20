package com.shukriev.merchantplatform.service.security.impl;

import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.service.security.AppRole;
import com.shukriev.merchantplatform.service.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
	private final MerchantService merchantService;

	@Autowired
	public UserServiceImpl(final MerchantService merchantService) {
		this.merchantService = merchantService;
	}

	@Override
	public UserDetailsService userDetailsService() {
		return email -> {
			final var merchant = merchantService.getByEmail(email)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			return User.builder()
					.accountExpired(false)
					.accountLocked(false)
					.disabled(false)
					.username(merchant.getEmail())
					.password(merchant.getPassword())
					.roles(AppRole.fromValue(merchant.getClass()).name())
					.build();
		};
	}
}
