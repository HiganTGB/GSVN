package com.tgb.gsvnbackend.model.entity;

import com.tgb.gsvnbackend.model.enumeration.CartStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Document(collection = "carts")
public class Cart extends AbstractMappedEntity {
    @Id
    private String cardId;
    private String userId;
    private CartStatus status;
    private List<CartItem> cartItems;
}
