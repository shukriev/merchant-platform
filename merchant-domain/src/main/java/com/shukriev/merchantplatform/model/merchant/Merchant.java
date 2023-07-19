package com.shukriev.merchantplatform.model.merchant;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "app_role")
public abstract class Merchant {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	//TODO add UUID validator
	private UUID id;
	@NotBlank
	@Email(regexp = ".+@.+\\..+")
	private String email;
	@NotBlank
	private String name;
	@NotBlank
	@Size(min = 5, message = "The password should not be shorten than 5 characters")
	//TODO some more validations can be added
	private String password;
	private ActiveInactiveStatusEnum status;

	protected Merchant() {
	}

	protected Merchant(final String email, final String name, final String password,
					   final ActiveInactiveStatusEnum status) {
		//ID has to be auto generated
		this.email = email;
		this.name = name;
		this.password = password;
		this.status = status;
	}

	protected Merchant(final UUID id, final String email, final String name, final String password,
					   final ActiveInactiveStatusEnum status) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.password = password;
		this.status = status;
	}

	public UUID getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public ActiveInactiveStatusEnum getStatus() {
		return status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Merchant merchant)) {
			return false;
		}

		return Objects.equals(id, merchant.id) &&
				Objects.equals(email, merchant.email) &&
				Objects.equals(name, merchant.name) &&
				Objects.equals(password, merchant.password) &&
				status == merchant.status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, email, name, password, status);
	}
}
