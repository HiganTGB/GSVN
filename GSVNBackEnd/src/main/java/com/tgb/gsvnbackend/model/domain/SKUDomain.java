package com.tgb.gsvnbackend.model.domain;

import com.tgb.gsvnbackend.model.enumeration.Status;
import com.tgb.gsvnbackend.model.enumeration.Type;

import java.math.BigDecimal;
import java.util.Date;

public class SKUDomain {
    private Integer skuId;
    private BigDecimal price;
    private Type type;
    private Date startOrder;
    private Date endOrder;
    private Integer stock;
    private Status status;
    private boolean isDeleted;
}
