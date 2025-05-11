package com.tgb.gsvnbackend.service;

import com.tgb.gsvnbackend.model.domain.SPUDomain;
import com.tgb.gsvnbackend.model.dto.SPUDTO;

public interface SPUService {
    SPUDTO create(SPUDTO spudto);
    SPUDTO update(int id, SPUDTO spudto);
    void delete(int id);
    boolean exists(int id);
    SPUDomain getDomain(int id);
}