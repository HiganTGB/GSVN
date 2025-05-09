package com.tgb.gsvnbackend.model.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SKUAttributeDTO {
    private Integer id;
    private Map<String, Object> attrs;
}