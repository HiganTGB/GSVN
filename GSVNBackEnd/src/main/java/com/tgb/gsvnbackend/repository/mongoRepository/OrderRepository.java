package com.tgb.gsvnbackend.repository.mongoRepository;

import com.tgb.gsvnbackend.model.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order,String> {
}
