package com.gsvn.promotionservice.common;


public interface IBaseService<REQ, RES, ID> {
    RES create(REQ request);

    RES update(ID id, REQ request);

    RES getById(ID id);

    void delete(ID id);
}