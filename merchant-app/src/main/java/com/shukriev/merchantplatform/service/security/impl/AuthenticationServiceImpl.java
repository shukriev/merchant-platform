package com.shukriev.merchantplatform.service.security.impl;

import com.shukriev.merchantplatform.controller.authentication.dto.JwtResponse;
import com.shukriev.merchantplatform.controller.authentication.dto.MerchantAuthDetails;
import com.shukriev.merchantplatform.controller.authentication.dto.SignInRequest;
import com.shukriev.merchantplatform.exception.merchant.MerchantNotFoundException;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.service.security.AppRole;
import com.shukriev.merchantplatform.service.security.AuthenticationService;
import com.shukriev.merchantplatform.service.security.JwtService;
import com.shukriev.merchantplatform.service.security.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
	private final MerchantService merchantService;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final UserService userService;

	public AuthenticationServiceImpl(final MerchantService merchantService,
									 final PasswordEncoder passwordEncoder,
									 final JwtService jwtService,
									 final AuthenticationManager authenticationManager,
									 final UserService userService) {
		this.merchantService = merchantService;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
		this.userService = userService;
	}

	@Override
	public JwtResponse signIn(SignInRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password()));
		final var merchantUser = merchantService.getByEmail(request.email())
				.map(merchant -> User.builder()
						.accountExpired(false)
						.accountLocked(false)
						.disabled(false)
						.username(merchant.getEmail())
						.password(passwordEncoder.encode(merchant.getPassword()))
						.roles(AppRole.fromValue(merchant.getClass()).name())
						.build())
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

		final var jwt = jwtService.generateToken(merchantUser);
		return new JwtResponse(jwt);
	}

	@Override
	public MerchantAuthDetails getMerchantDetails() {
		return Optional.ofNullable(userService.getLoggedMerchant())
				.map(MerchantAuthDetails::of)
				.orElseThrow(() -> new MerchantNotFoundException("Merchant Not Found!"));
	}
}
