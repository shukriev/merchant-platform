package com.shukriev.merchantplatform.config;

import com.shukriev.merchantplatform.MerchantInfraMain;
import com.shukriev.merchantplatform.adapter.merchant.MerchantProviderImpl;
import com.shukriev.merchantplatform.adapter.transaction.TransactionProviderImpl;
import com.shukriev.merchantplatform.outbound.merchant.MerchantProvider;
import com.shukriev.merchantplatform.outbound.transaction.TransactionProvider;
import com.shukriev.merchantplatform.repository.postgres.merchant.MerchantRepository;
import com.shukriev.merchantplatform.repository.postgres.transaction.TransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.shukriev.merchantplatform.repository")
@ComponentScan(basePackageClasses = MerchantInfraMain.class)
public class BeanConfig {

	@Bean
	MerchantProvider merchantProvider(final MerchantRepository merchantRepository) {
		return new MerchantProviderImpl(merchantRepository);
	}

	@Bean
	TransactionProvider transactionProvider(final TransactionRepository transactionRepository) {
		return new TransactionProviderImpl(transactionRepository);
	}
}
