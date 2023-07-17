package com.shukriev.merchantplatform.model.transaction;

import com.shukriev.merchantplatform.exception.merchant.MerchantTransactionSumInsufficient;
import com.shukriev.merchantplatform.exception.transaction.TransactionValidationException;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Objects;
import java.util.UUID;

@Entity
@DiscriminatorValue("Refund")
public final class RefundTransaction extends Transaction {
	@Override
	public void validateReferenceTransaction() {
		if (Objects.isNull(getReference()) || !ChargeTransaction.class.equals(getReference().getClass())) {
			throw new TransactionValidationException("Refund transaction should contain reference to Charge transaction");
		}

		if (this.getMerchant().getTotalTransactionSum() < this.getAmount()) {
			throw new MerchantTransactionSumInsufficient();
		}
	}

	@Override
	public Transaction updateStatus(TransactionStatusEnum status) {
		return new RefundTransaction(this.getId(), this.getMerchant(), this.getAmount(), status, this.getCustomerEmail(), this.getCustomerPhone(), this.getReference());
	}

	public RefundTransaction() {
	}

	public RefundTransaction(UUID id, NormalMerchant merchant, Double amount, TransactionStatusEnum status, String customerEmail, String customerPhone, Transaction reference) {
		super(id, merchant, amount, status, customerEmail, customerPhone, reference);
	}
}
