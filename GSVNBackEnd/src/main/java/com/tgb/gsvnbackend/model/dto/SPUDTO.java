package com.tgb.gsvnbackend.model.dto;

import com.tgb.gsvnbackend.model.enumeration.Status;
import com.tgb.gsvnbackend.model.enumeration.Type;
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
public class SPUDTO {
    private Integer spuId;
    private String title;
    private String description;
    private Type type;
    private Date startOrder;
    private Date endOrder;
    private int categoryId;
    private int fandomId;
    private int brandId;
    private Status status;
    private int sort;
    private boolean isDeleted;
    private Map<String, Object> attrs;
}