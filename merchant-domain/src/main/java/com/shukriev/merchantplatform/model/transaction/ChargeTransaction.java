package com.shukriev.merchantplatform.model.transaction;

import com.shukriev.merchantplatform.exception.transaction.TransactionValidationException;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Objects;
import java.util.UUID;

@Entity
@DiscriminatorValue("Charge")
public final class ChargeTransaction extends Transaction {
	@Override
	public void validateReferenceTransaction() {
		if (Objects.isNull(getReference()) || !AuthorizeTransaction.class.equals(getReference().getClass())) {
			throw new TransactionValidationException(
					"Charge transaction should contain reference to Authorize transaction");
		}
	}

	@Override
	public Transaction updateStatus(final TransactionStatusEnum status) {
		return new ChargeTransaction(this.getId(), this.getMerchant(), this.getAmount(), status,
				this.getCustomerEmail(), this.getCustomerPhone(), this.getReference());
	}

	public ChargeTransaction() {
	}

	public ChargeTransaction(final UUID id, final NormalMerchant merchant, final Double amount,
							 final TransactionStatusEnum status, final String customerEmail,
							 final String customerPhone, final Transaction reference) {
		super(id, merchant, amount, status, customerEmail, customerPhone, reference);
	}
}
