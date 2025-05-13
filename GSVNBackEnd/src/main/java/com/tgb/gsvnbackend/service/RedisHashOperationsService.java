package com.tgb.gsvnbackend.service;

import java.util.Map;

public interface RedisHashOperationsService {
    void hSet(String key, String field, Object value);

    void hmSet(String key, Map<Object, Object> map);

    Object hGet(String key, String field);

    Map<Object, Object> hGetAll(String key);

    boolean hExists(String key, String field);

    Long hDel(String key, Object... fields);

    Integer hIncrBy(String key, Object field, long delta);
    public void deleteByKey(String key);
}
