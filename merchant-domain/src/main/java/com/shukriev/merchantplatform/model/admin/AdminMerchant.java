package com.shukriev.merchantplatform.model.admin;

import com.shukriev.merchantplatform.model.merchant.Merchant;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Admin")
public final class AdminMerchant extends Merchant {
}
