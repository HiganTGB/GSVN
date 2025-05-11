package com.tgb.gsvnbackend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@Table(name = "spu_sku",schema = "product")
public class SPUSKU extends AbstractMappedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( unique = true, nullable = false, updatable = false)
    private Integer id;
    @Column(name = "spu_id")
    private String spuId;
    @Column(name = "sku_id")
    private String skuId;
    @Column(name = "is_deleted")
    private boolean isDeleted;
}
