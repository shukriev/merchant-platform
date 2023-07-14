package com.shukriev.merchantplatform.config;

import com.shukriev.merchantplatform.MerchantInfraMain;
import com.shukriev.merchantplatform.adapter.MerchantProviderImpl;
import com.shukriev.merchantplatform.outbound.merchant.MerchantProvider;
import com.shukriev.merchantplatform.repository.postgres.MerchantRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.shukriev.merchant.repository")
@ComponentScan(basePackageClasses = MerchantInfraMain.class)
public class BeanConfig {

	@Bean
	MerchantProvider merchantProvider(final MerchantRepository merchantRepository) {
		return new MerchantProviderImpl(merchantRepository);
	}
}
