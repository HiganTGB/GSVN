package com.tgb.gsvnbackend.service;

import com.tgb.gsvnbackend.model.dto.SKUAttributeDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SKUAttributeService {
    SKUAttributeDTO read(int id);
    Page<SKUAttributeDTO> readByPage(int page, int size, String sortBy, String sortDirection);
    List<SKUAttributeDTO> searchByAttrs(String key, String value);
    SKUAttributeDTO create(SKUAttributeDTO skuAttributeDTO);
    SKUAttributeDTO update(int id, SKUAttributeDTO skuAttributeDTO);
    void delete(int id);
}
