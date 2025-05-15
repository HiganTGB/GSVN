package com.tgb.gsvnbackend.repository.jpaRepository;

import com.tgb.gsvnbackend.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {
}
