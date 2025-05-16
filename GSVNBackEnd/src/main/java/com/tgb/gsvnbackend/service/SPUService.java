package com.tgb.gsvnbackend.service;

import com.tgb.gsvnbackend.model.domain.SPUDomain;
import com.tgb.gsvnbackend.model.dto.SPUDTO;

import java.util.Map;

public interface SPUService {
    SPUDTO create(SPUDTO spudto);
    SPUDTO update(int id, SPUDTO spudto);
    void delete(int id);
    boolean exists(int id);
    SPUDomain getDomain(int id);
     void updateSyncAttributes(int id, Map<String,Object> attributes);
}