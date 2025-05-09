package com.tgb.gsvnbackend.model.dto;

import com.tgb.gsvnbackend.model.enumeration.Status;
import com.tgb.gsvnbackend.model.enumeration.Type;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SKUDTO {
    private Integer sku_id;
    private String no;
    private String title;
    private int category_id;
    private int fandom_id;
    private int brand_id;
    private Type type;
    private Date start_order;
    private Date end_order;
    private Integer stock;
    private BigDecimal price;
    private Status status;
    private int sort;
    private boolean is_deleted;
}