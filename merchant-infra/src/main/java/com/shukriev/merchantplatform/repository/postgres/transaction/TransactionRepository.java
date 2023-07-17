package com.shukriev.merchantplatform.repository.postgres.transaction;

import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import com.shukriev.merchantplatform.model.transaction.Transaction;
import com.shukriev.merchantplatform.model.transaction.TransactionStatusEnum;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
	@Transactional
	@Modifying
	@Query("delete from Transaction t where t.createdAt < ?1")
	void deleteByCreatedAtBefore(LocalDateTime createdAt);
	@Transactional
	@Modifying
	@Query("""
			update Transaction t set t.id = :id, t.merchant = :merchant, t.amount = :amount, t.status = :status, t.customerEmail = :customerEmail, t.customerPhone = :customerPhone, t.reference = :reference
			where t.id = :id""")
	void update(@Param("id") UUID id, @Param("merchant") NormalMerchant merchant, @Param("amount") Double amount, @Param("status") TransactionStatusEnum status, @Param("customerEmail") String customerEmail, @Param("customerPhone") String customerPhone, @Param("reference") Transaction reference);

	Set<Transaction> findByMerchant_Id(UUID id);
}