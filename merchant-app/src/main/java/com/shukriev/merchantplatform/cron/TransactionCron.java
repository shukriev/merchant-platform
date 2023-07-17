package com.shukriev.merchantplatform.cron;

import com.shukriev.merchantplatform.inbound.transaction.TransactionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionCron {
	private final TransactionService transactionService;

	public TransactionCron(final TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@Scheduled(cron = "0 0 * * * *")
	public void deleteOldTransactions() {
		transactionService.deleteByTimestampBefore(LocalDateTime.now().plusHours(1));
	}
}
