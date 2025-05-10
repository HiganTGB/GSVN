package com.tgb.gsvnbackend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@Table(name = "brands",schema = "brand")
public class Brand extends AbstractMappedEntity {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id", unique = true, nullable = false, updatable = false)
    private Integer brandId;
    @Column(name = "brand_title")
    private String title;
    @Column(name = "brand_email", unique = true)
    private String email;
}
