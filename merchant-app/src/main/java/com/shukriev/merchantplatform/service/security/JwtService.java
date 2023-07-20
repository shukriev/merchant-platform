package com.shukriev.merchantplatform.service.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
	String extractUserEmail(String token);

	String generateToken(UserDetails userDetails);

	boolean isTokenValid(String token, UserDetails userDetails);
}
