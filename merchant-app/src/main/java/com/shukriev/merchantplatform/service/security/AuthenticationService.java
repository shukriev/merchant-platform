package com.shukriev.merchantplatform.service.security;

import com.shukriev.merchantplatform.controller.authentication.dto.JwtResponse;
import com.shukriev.merchantplatform.controller.authentication.dto.SignInRequest;

public interface AuthenticationService {
	JwtResponse signIn(SignInRequest request);
}
