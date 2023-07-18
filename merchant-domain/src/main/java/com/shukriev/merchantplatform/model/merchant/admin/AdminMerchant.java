package com.shukriev.merchantplatform.model.merchant.admin;

import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.Merchant;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue("Admin")
public final class AdminMerchant extends Merchant {
	public AdminMerchant() {
	}

	public AdminMerchant(UUID id, String email, String name, String password, ActiveInactiveStatusEnum status) {
		super(id, email, name, password, status);
	}

}
