package com.shukriev.merchantplatform.config;

import com.shukriev.merchantplatform.MerchantPlatformApplication;
import com.shukriev.merchantplatform.adapter.MerchantProviderImpl;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.inbound.merchant.MerchantServiceImpl;
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
}
