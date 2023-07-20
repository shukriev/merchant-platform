package com.shukriev.merchantplatform.repository.postgres.merchant;

import com.shukriev.merchantplatform.model.merchant.ActiveInactiveStatusEnum;
import com.shukriev.merchantplatform.model.merchant.Merchant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MerchantRepository extends CrudRepository<Merchant, UUID> {
	@Query("select m from Merchant m where m.email = ?1 and m.status = ?2")
	Optional<Merchant> findByEmailAndStatus(String email, ActiveInactiveStatusEnum status);
}
