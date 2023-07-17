package com.shukriev.merchantplatform.config;

import com.shukriev.merchantplatform.MerchantPlatformApplication;
import com.shukriev.merchantplatform.adapter.merchant.MerchantProviderImpl;
import com.shukriev.merchantplatform.adapter.transaction.TransactionProviderImpl;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.inbound.merchant.MerchantServiceImpl;
import com.shukriev.merchantplatform.inbound.transaction.TransactionService;
import com.shukriev.merchantplatform.inbound.transaction.TransactionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = MerchantPlatformApplication.class)
public class AppConfig {
	@Bean
	MerchantService merchantService(final MerchantProviderImpl merchantProvider) {
		return new MerchantServiceImpl(merchantProvider);
	}

	@Bean
	TransactionService transactionService(final TransactionProviderImpl transactionProvider,
										  final MerchantProviderImpl merchantProvider) {
		return new TransactionServiceImpl(transactionProvider, merchantProvider);
	}
}
