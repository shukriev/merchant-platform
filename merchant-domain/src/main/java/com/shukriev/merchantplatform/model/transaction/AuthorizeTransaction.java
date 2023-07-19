package com.shukriev.merchantplatform.model.transaction;

import com.shukriev.merchantplatform.exception.transaction.TransactionValidationException;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Objects;
import java.util.UUID;

@Entity
@DiscriminatorValue("Authorize")
public class AuthorizeTransaction extends Transaction {
	@Override
	public void validateReferenceTransaction() {
		if (Objects.nonNull(getReference())) {
			throw new TransactionValidationException("Authorize transaction should not contain reference");
		}
	}

	@Override
	public Transaction updateStatus(final TransactionStatusEnum status) {
		return new AuthorizeTransaction(this.getId(), this.getMerchant(), this.getAmount(), status,
				this.getCustomerEmail(), this.getCustomerPhone(), this.getReference());
	}

	public AuthorizeTransaction() {
	}

	public AuthorizeTransaction(final UUID id, final NormalMerchant merchant, final Double amount,
								final TransactionStatusEnum status, final String customerEmail,
								final String customerPhone, final Transaction reference) {
		super(id, merchant, amount, status, customerEmail, customerPhone, reference);
	}
}
