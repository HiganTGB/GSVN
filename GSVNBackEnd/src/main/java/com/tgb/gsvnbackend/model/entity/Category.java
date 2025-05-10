package com.tgb.gsvnbackend.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;


@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@Table(name = "categories",schema = "category")
public class Category extends AbstractMappedEntity {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", unique = true, nullable = false, updatable = false)
    private Integer categoryId;
    @Column(name = "category_title")
    private String title;
    @Column(name = "category_parent_id")
    private int parentID;
}
