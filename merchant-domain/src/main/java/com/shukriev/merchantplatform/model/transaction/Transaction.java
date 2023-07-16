package com.shukriev.merchantplatform.model.transaction;

import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transaction_type")
public abstract class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	//TODO add UUID validator
	private UUID id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id")
	private NormalMerchant merchant;
	@DecimalMin(value = "0", inclusive = false, message = "The amount must be larger than 0")
	private Double amount;
	@NotNull(message = "Transaction status is required")
	private TransactionStatusEnum status;
	@NotBlank
	@Email(message = "Invalid customer email address")
	private String customerEmail;
	@Pattern(regexp = "(\\+359|0)[0-9]{9}", message = "Wrong country code provided. It has to be +359 or starting with 0")
	private String customerPhone;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reference_id")
	private Transaction reference;

	protected Transaction() {
	}

	protected Transaction(UUID id, NormalMerchant merchant, Double amount, TransactionStatusEnum status, String customerEmail, String customerPhone, Transaction reference) {
		this.id = id;
		this.merchant = merchant;
		this.amount = amount;
		this.status = status;
		this.customerEmail = customerEmail;
		this.customerPhone = customerPhone;
		this.reference = reference;
	}

	public UUID getId() {
		return id;
	}

	public NormalMerchant getMerchant() {
		return merchant;
	}

	public Double getAmount() {
		return amount;
	}

	public TransactionStatusEnum getStatus() {
		return status;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public Transaction getReference() {
		return reference;
	}

	public String getTransactionType() {
		final var discriminatorValue = getClass().getAnnotation(DiscriminatorValue.class);
		if (discriminatorValue != null) {
			return discriminatorValue.value();
		}
		return null;
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Transaction that)) return false;
		return Objects.equals(id, that.id) && Objects.equals(merchant, that.merchant) && Objects.equals(amount, that.amount) && status == that.status && Objects.equals(customerEmail, that.customerEmail) && Objects.equals(customerPhone, that.customerPhone) && Objects.equals(reference, that.reference);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, merchant, amount, status, customerEmail, customerPhone, reference);
	}
}
