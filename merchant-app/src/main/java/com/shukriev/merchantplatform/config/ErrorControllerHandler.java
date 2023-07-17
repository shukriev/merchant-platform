package com.shukriev.merchantplatform.config;

import com.shukriev.merchantplatform.exception.InvalidParameterException;
import com.shukriev.merchantplatform.exception.merchant.MerchantInactiveException;
import com.shukriev.merchantplatform.exception.merchant.MerchantNotFoundException;
import com.shukriev.merchantplatform.exception.transaction.TransactionNotFoundException;
import com.shukriev.merchantplatform.exception.transaction.TransactionValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

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

	@ExceptionHandler(TransactionValidationException.class)
	public ResponseEntity<String> transactionValidationException(final TransactionValidationException exception) {
		return ResponseEntity
				.badRequest()
				.body(exception.getMessage());
	}

	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<String> transactionNotFoundException(final TransactionNotFoundException exception) {
		return ResponseEntity
				.badRequest()
				.body(exception.getMessage());
	}


	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception) {
		return ResponseEntity
				.badRequest()
				.body(exception.getConstraintViolations().stream()
						.map(ConstraintViolation::getMessage)
						.collect(Collectors.joining(" | ")));
	}
}
