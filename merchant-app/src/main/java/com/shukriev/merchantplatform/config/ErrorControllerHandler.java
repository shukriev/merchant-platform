package com.shukriev.merchantplatform.config;

import com.shukriev.merchantplatform.exception.InvalidParameterException;
import com.shukriev.merchantplatform.exception.merchant.MerchantInactiveException;
import com.shukriev.merchantplatform.exception.merchant.MerchantNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorControllerHandler {
	@ExceptionHandler(MerchantNotFoundException.class)
	public ResponseEntity<String> merchantNotFoundExceptionHandler(final MerchantNotFoundException exception) {
		return ResponseEntity
				.noContent()
				.build();
	}

	@ExceptionHandler(InvalidParameterException.class)
	public ResponseEntity<String> invalidParameterException(final InvalidParameterException exception) {
		return ResponseEntity
				.badRequest()
				.body(exception.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> illegalArgumentException(final IllegalArgumentException exception) {
		return ResponseEntity
				.badRequest()
				.body(exception.getMessage());
	}

	@ExceptionHandler(MerchantInactiveException.class)
	public ResponseEntity<String> merchantInactiveException(final MerchantInactiveException exception) {
		return ResponseEntity
				.badRequest()
				.body(exception.getMessage());
	}
}
