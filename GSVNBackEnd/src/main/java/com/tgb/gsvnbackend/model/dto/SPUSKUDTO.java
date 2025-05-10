package com.tgb.gsvnbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SPUSKUDTO {
    private Integer id;
    private String spuId;
    private String skuId;
    private boolean isDeleted;
}