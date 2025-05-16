package com.tgb.gsvnbackend.service;

import com.tgb.gsvnbackend.model.dto.SKUAttributeDTO;
import com.tgb.gsvnbackend.model.dto.SKUDTO;
import com.tgb.gsvnbackend.model.dto.SPUSKUDTO;

import java.util.List;

public interface SKUService {
    SKUDTO create(SKUDTO skuDTO);
    SKUDTO update(int id, SKUDTO skuDTO);
    void delete(int id);
    SKUDTO read(int id);
    List<SPUSKUDTO> getListBySpuID(int spu_id);
}
