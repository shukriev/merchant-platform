package com.shukriev.merchantplatform.controller.authentication;

import com.shukriev.merchantplatform.controller.authentication.dto.SignInRequest;
import com.shukriev.merchantplatform.service.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	@Autowired
	public AuthenticationController(final AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@PostMapping("/signin")
	public ResponseEntity<?> signIn(@RequestBody SignInRequest request) {
		return ResponseEntity.ok(authenticationService.signIn(request));
	}

	@GetMapping("/details")
	public ResponseEntity<?> getMerchantDetails() {
		return ResponseEntity.ok(authenticationService.getMerchantDetails());
	}
}
