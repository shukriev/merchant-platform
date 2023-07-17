package com.shukriev.merchantplatform.model.transaction;

import com.shukriev.merchantplatform.exception.transaction.TransactionValidationException;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Objects;
import java.util.UUID;

@Entity
@DiscriminatorValue("Reversal")
public final class ReversalTransaction extends Transaction {
	@Override
	public void validateReferenceTransaction() {
		if (Objects.isNull(getReference()) || !AuthorizeTransaction.class.equals(getReference().getClass())) {
			throw new TransactionValidationException("Reversal transaction should contain reference to Authorize transaction");
		}
	}

	@Override
	public Transaction updateStatus(TransactionStatusEnum status) {
		return new ReversalTransaction(this.getId(), this.getMerchant(), this.getAmount(), status, this.getCustomerEmail(), this.getCustomerPhone(), this.getReference());
	}

	public ReversalTransaction() {
	}

	public ReversalTransaction(UUID id, NormalMerchant merchant, Double amount, TransactionStatusEnum status, String customerEmail, String customerPhone, Transaction reference) {
		super(id, merchant, amount, status, customerEmail, customerPhone, reference);
	}
}
