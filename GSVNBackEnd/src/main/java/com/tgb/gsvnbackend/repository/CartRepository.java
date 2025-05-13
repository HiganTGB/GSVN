package com.tgb.gsvnbackend.repository;

import com.tgb.gsvnbackend.model.entity.Cart;
import com.tgb.gsvnbackend.model.enumeration.CartStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart,String> {
    Optional<Cart> findByUserIdAndStatus(String userId, CartStatus status);
}
