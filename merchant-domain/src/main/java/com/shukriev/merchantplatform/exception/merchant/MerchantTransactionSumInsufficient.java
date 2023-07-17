package com.shukriev.merchantplatform.exception.merchant;

public final class MerchantTransactionSumInsufficient extends RuntimeException {
	public MerchantTransactionSumInsufficient() {
		super("Merchant transaction sum is insufficient");
	}
}
