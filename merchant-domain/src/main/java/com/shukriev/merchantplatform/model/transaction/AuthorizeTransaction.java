package com.shukriev.merchantplatform.model.transaction;

import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue("Authorize")
public class AuthorizeTransaction extends Transaction {
	public AuthorizeTransaction() {
	}
	public AuthorizeTransaction(UUID id, NormalMerchant merchant, Double amount, TransactionStatusEnum status, String customerEmail, String customerPhone, Transaction reference) {
		super(id, merchant, amount, status, customerEmail, customerPhone, reference);
	}
}
