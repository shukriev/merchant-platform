package com.shukriev.merchantplatform.model.merchant;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;

import java.util.Objects;
import java.util.UUID;

@Entity
@DiscriminatorValue("Normal")
public final class NormalMerchant extends Merchant {
	private String description;

	@Min(0)
	private Double totalTransactionSum;

	public NormalMerchant() {
	}

	public NormalMerchant(String email, String name, String password, ActiveInactiveStatusEnum status, String description, Double totalTransactionSum) {
		super(email, name, password, status);
		this.description = description;
		this.totalTransactionSum = totalTransactionSum;
	}

	/**
	 * The following constructor is created for testing only purpose
	 *
	 * @param id                  - id
	 * @param email               - email
	 * @param name                - name
	 * @param password            - password
	 * @param status              - status
	 * @param description         - description
	 * @param totalTransactionSum - totalTransactionSum
	 */
	public NormalMerchant(UUID id, String email, String name, String password, ActiveInactiveStatusEnum status, String description, Double totalTransactionSum) {
		super(id, email, name, password, status);
		this.description = description;
		this.totalTransactionSum = totalTransactionSum;
	}

	public String getDescription() {
		return description;
	}

	public Double getTotalTransactionSum() {
		return totalTransactionSum;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NormalMerchant merchant)) return false;
		return Objects.equals(description, merchant.description) && Objects.equals(totalTransactionSum, merchant.totalTransactionSum);
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, totalTransactionSum);
	}
}
