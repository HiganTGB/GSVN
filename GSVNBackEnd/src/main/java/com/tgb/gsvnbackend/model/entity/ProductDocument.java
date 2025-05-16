package com.tgb.gsvnbackend.model.entity;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import org.springframework.data.elasticsearch.annotations.Mapping;
import java.util.Date;
import java.util.Map;

@EqualsAndHashCode
@Document(indexName = "product")
@Data
@Mapping(mappingPath = "static/product.json")
public class ProductDocument {
    @Id
    private String spuId;

    private String title;


    private String description;


    private String type;


    private Date startOrder;

    private Date endOrder;


    private Integer categoryId;


    private Integer fandomId;


    private Integer brandId;


    private String status;

    private Integer sort;


    private Boolean isDeleted;


    private Map<String, Object> attrs;


    private Date createdAt;

    private Date updatedAt;


    private Long version;

    private Double lowestPrice;
}
