package com.shukriev.merchantplatform.rake;

import com.shukriev.merchantplatform.exception.RakeTaskException;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.factory.MerchantFactory;
import com.shukriev.merchantplatform.model.merchant.factory.MerchantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

@Component
public class MerchantInitializer implements CommandLineRunner {
	private final MerchantService merchantService;

	@Autowired
	public MerchantInitializer(final MerchantService merchantService) {
		this.merchantService = merchantService;
	}

	@Override
	public void run(String... args) {
		if (args.length == 1) {
			final var csvFilePath = args[0];
			try (final var reader = new BufferedReader(new FileReader(csvFilePath))) {
				String line;
				boolean isFirstLine = true;
				while (Objects.nonNull(line = reader.readLine())) {
					if (isFirstLine) {
						isFirstLine = false;
						continue; // Skip the header line
					}
					final var fields = line.split(",");
					if (fields.length == 7) {
						final var merchantType = MerchantType.fromValue(fields[0].trim());
						final var email = fields[1].trim();
						final var name = fields[2].trim();
						final var password = fields[3].trim();
						final var status = ActiveInactiveStatusEnum.fromValue(fields[4].trim());
						final var description = fields[5].trim();
						final var totalTransactionSum = Double.valueOf(fields[6].trim());
						//TODO the rake task will be creating the users everytime that we run the application
						//Might require fixing the functionality depending on the requirements
						merchantService.createMerchant(MerchantFactory.getMerchant(
								null,
								email,
								name,
								password,
								status,
								description,
								totalTransactionSum,
								merchantType
						));
					}
				}
			} catch (IOException e) {
				throw new RakeTaskException(e);
			}
		}
	}
}
