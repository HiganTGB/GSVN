package com.tgb.gsvnbackend.service;


import com.tgb.gsvnbackend.model.dto.FandomDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FandomService {
    FandomDTO read(int id);

    List<FandomDTO> readAll();
    FandomDTO create(FandomDTO fandomDTO);
    Page<FandomDTO> readByPage(int page, int size, String sortBy, String sortDirection);
    FandomDTO update(int id, FandomDTO fandomDTO);

    void delete(int id);

    List<FandomDTO> searchByTitle(String title);
}
