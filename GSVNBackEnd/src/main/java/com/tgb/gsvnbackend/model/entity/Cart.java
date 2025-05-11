package com.tgb.gsvnbackend.model.entity;

import com.tgb.gsvnbackend.model.enumeration.CartStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document
public class Cart extends AbstractMappedEntity {
    @Id
    String cardId;
    String userId;
    CartStatus status;
    List<CartItem> cartItems;
}
