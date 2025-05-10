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
    private Integer spu_id;
    private String title;
    private String description;
    private Type type;
    private Date start_order;
    private Date end_order;
    private int category_id;
    private int fandom_id;
    private int brand_id;
    private Status status;
    private int sort;
    private boolean is_deleted;
    private Map<String, Object> attrs;
}