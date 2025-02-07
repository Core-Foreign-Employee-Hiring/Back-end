package com.core.foreign.api.payment.repository;

import com.core.foreign.api.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByPaymentKey(String paymentKey);

    @Query("select p from Payment p" +
            " where p.member.id=:memberId")
    Page<Payment> findAllByMemberId(@Param("memberId")Long memberId, Pageable pageable);

}
