package com.tgb.gsvnbackend.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tgb.gsvnbackend.lib.JsonDataConverter;
import com.tgb.gsvnbackend.model.enumeration.Status;
import com.tgb.gsvnbackend.model.enumeration.Type;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@Table(name = "spu",schema = "product")
public class SPU extends AbstractMappedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spu_id", unique = true, nullable = false, updatable = false)
    private Integer spuId;
    @Column(name = "spu_title")
    private String title;
    @Column(name = "spu_description")
    private String description;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "spu_type")
    private Type type;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "start_order")
    private Date startOrder;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "end_order")
    private Date endOrder;

    @Column(name="category_id")
    private int categoryId;
    @Column(name="fandom_id")
    private int fandomId;
    @Column(name="brand_id")
    private int brandId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status;
    @Column(name = "sort")
    private int sort;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Convert(converter = JsonDataConverter.class)
    @Column(name = "spu_attrs",columnDefinition = "JSON")
    private Map<String, Object> attrs;

    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (startOrder != null && endOrder != null && startOrder.after(endOrder)) {
            throw new IllegalStateException("Start order date must be before or same as end order date");
        }
    }
}

