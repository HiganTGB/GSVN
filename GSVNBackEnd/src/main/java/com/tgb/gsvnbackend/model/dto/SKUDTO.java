package com.tgb.gsvnbackend.model.dto;

import com.tgb.gsvnbackend.model.enumeration.Status;
import com.tgb.gsvnbackend.model.enumeration.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SKUDTO {
    private Integer skuId;
    private String no;
    private String title;
    private int categoryId;
    private int fandomId;
    private int brandId;
    private Type type;
    private Date startOrder;
    private Date endOrder;
    private Integer stock;
    private BigDecimal price;
    private Status status;
    private int sort;
    private boolean isDeleted;

    private Integer spuId;
    private Map<String, Object> attrs;
}