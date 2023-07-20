package com.shukriev.merchantplatform.service.security;

import com.shukriev.merchantplatform.model.merchant.Merchant;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
	UserDetailsService userDetailsService();

	Merchant getLoggedMerchant();
}