package com.shukriev.merchantplatform.controller.transaction;

import com.shukriev.merchantplatform.controller.transaction.dto.CreateTransactionDTO;
import com.shukriev.merchantplatform.service.transaction.TransactionAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/transactions")
public class TransactionController {

	private final TransactionAppService transactionService;

	@Autowired
	public TransactionController(final TransactionAppService transactionService) {
		this.transactionService = transactionService;
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createTransaction(@RequestBody final CreateTransactionDTO transaction) {
		return new ResponseEntity<>(transactionService.createTransaction(transaction), HttpStatus.OK);
	}
}
