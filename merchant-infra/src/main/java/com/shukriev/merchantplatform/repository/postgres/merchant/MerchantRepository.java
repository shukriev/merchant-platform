package com.shukriev.merchantplatform.repository.postgres.merchant;

import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MerchantRepository extends CrudRepository<NormalMerchant, UUID> {
}
