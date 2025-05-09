package com.tgb.gsvnbackend.service;

import com.tgb.gsvnbackend.model.dto.BrandDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BrandService {
    BrandDTO read(int id);

    Page<BrandDTO> readByPage(int page, int size, String sortBy, String sortDirection);

    List<BrandDTO> searchByTitle(String title);

    BrandDTO create(BrandDTO brandDTO);

    BrandDTO update(int id, BrandDTO brandDTO);

    void delete(int id);
}
