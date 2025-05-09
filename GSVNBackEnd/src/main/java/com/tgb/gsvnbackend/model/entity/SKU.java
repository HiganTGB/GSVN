package com.tgb.gsvnbackend.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tgb.gsvnbackend.lib.JsonDataConverter;
import com.tgb.gsvnbackend.model.enumeration.Status;
import com.tgb.gsvnbackend.model.enumeration.Type;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@Table(name = "sku",schema = "product")
public class SKU extends AbstractMappedEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_id", unique = true, nullable = false, updatable = false)
    private Integer sku_id;
    @Column(name = "sku_no",unique = true)
    private String no;
    @Column(name = "sku_title")
    private String title;

    @Column(name="category_id")
    private int category_id;
    @Column(name="fandom_id")
    private int fandom_id;
    @Column(name="brand_id")
    private int brand_id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "sku_type")
    private Type type;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "start_order")
    private Date start_order;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "end_order")
    private Date end_order;
    @Column(name = "sku_stock")
    private Integer stock;
    @Column(name = "sku_price", columnDefinition = "decimal(8,2)")
    private BigDecimal price;
    @Convert(converter = JsonDataConverter.class)
    @Column(name = "sku_attrs",columnDefinition = "JSON")
    private Map<String, Object> attrs;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status;
    @Column(name = "sort")
    private int sort;
    @Column(name = "is_deleted")
    private boolean is_deleted;
}
