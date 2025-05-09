package com.tgb.gsvnbackend.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tgb.gsvnbackend.lib.JsonDataConverter;
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
@Table(name = "sku_attr",schema = "product")
public class SKUAttribute extends AbstractMappedEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Integer id;
    @Convert(converter = JsonDataConverter.class)
    @Column(name = "sku_attrs",columnDefinition = "JSON")
    private Map<String, Object> attrs;
}
