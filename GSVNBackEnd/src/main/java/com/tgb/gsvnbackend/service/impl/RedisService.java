package com.tgb.gsvnbackend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgb.gsvnbackend.service.CachingService;
import com.tgb.gsvnbackend.service.RedisHashOperationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService<T extends Serializable> implements CachingService, RedisHashOperationsService {

    private RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, Object, Object> hashOperations;
    private final long DEFAULT_CACHE_TIME = 600;
    private ObjectMapper objectMapper;

    @Autowired
    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }
    public <T> void setPageData(String key, int page, int size, List<T> data, Class<T> clazz) {
        try {
            String pageKey = String.format("%s:page:%d:size:%d", key, page, size);
            String value = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(pageKey, value, DEFAULT_CACHE_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu dữ liệu phân trang vào Redis", e);
        }
    }
    public <T> List<T> getPageData(String key, int page, int size, Class<T> clazz) {
        try {
            String pageKey = String.format("%s:page:%d:size:%d", key, page, size);
            String value = redisTemplate.opsForValue().get(pageKey);
            if (value != null) {
                return objectMapper.readValue(value, new TypeReference<List<T>>() {});
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy dữ liệu phân trang từ Redis", e);
        }
    }
    public <T> void saveById(String key, Integer id, T data, Class<T> clazz) {
        try {
            String idKey = String.format("%s:%d", key, id);
            String value = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(idKey, value, DEFAULT_CACHE_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu dữ liệu theo ID vào Redis", e);
        }
    }

    public <T> T getById(String key, Integer id, Class<T> clazz) {
        try {
            String idKey = String.format("%s:%d", key, id);
            String value = redisTemplate.opsForValue().get(idKey);
            if (value != null) {
                return objectMapper.readValue(value, clazz);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy dữ liệu theo ID từ Redis", e);
        }
    }
    public void deleteById(String key, Integer id) {
        try {
            String idKey = String.format("%s:%d", key, id);
            redisTemplate.delete(idKey);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa dữ liệu theo ID từ Redis", e);
        }
    }
    public void deleteById(String key, String  id) {
        try {
            String idKey = String.format("%s:%s", key, id);
            redisTemplate.delete(idKey);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa dữ liệu theo ID từ Redis", e);
        }
    }
    public <T> void saveListBySearchValue(String key, String searchValue, List<T> data, Class<T> clazz) {
        try {
            String searchKey = String.format("%s:search:%s", key, searchValue);
            String value = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(searchKey, value, DEFAULT_CACHE_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu danh sách theo giá trị tìm kiếm vào Redis", e);
        }
    }
    public <T> List<T> getListBySearchValue(String key, String searchValue, Class<T> clazz) {
        try {
            String searchKey = String.format("%s:search:%s", key, searchValue);
            String value = redisTemplate.opsForValue().get(searchKey);
            if (value != null) {
                return objectMapper.readValue(value, new TypeReference<List<T>>() {
                });
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách theo giá trị tìm kiếm từ Redis", e);
        }
    }
    public void hSet(String key, String field, Object value) {
        hashOperations.put(key, field, value);
    }
    public void hmSet(String key, Map<Object, Object> map) {
        hashOperations.putAll(key, map);
    }
    public Object hGet(String key, String field) {
        return hashOperations.get(key, field);
    }
    public Map<Object, Object> hGetAll(String key) {
        return hashOperations.entries(key);
    }
    public boolean hExists(String key, String field) {
        return hashOperations.hasKey(key, field);
    }
    public Long hDel(String key, Object... fields) {
        return hashOperations.delete(key, fields);
    }
    public Integer hIncrBy(String key, Object field, long delta) {
        Long result = hashOperations.increment(key, field, delta);
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            throw new ArithmeticException("Result is out of Integer range");
        }
        return result.intValue();
    }
    public void deleteByKey(String key) {
        redisTemplate.delete(key);
    }
    public <T> void saveById(String key, String id, T data, Class<T> clazz) {
        try {
            String idKey = String.format("%s:%s", key, id);
            String value = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(idKey, value, DEFAULT_CACHE_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu dữ liệu theo ID vào Redis", e);
        }
    }

    public <T> T getById(String key, String id, Class<T> clazz) {
        try {
            String idKey = String.format("%s:%s", key, id);
            String value = redisTemplate.opsForValue().get(idKey);
            if (value != null) {
                return objectMapper.readValue(value, clazz);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy dữ liệu theo ID từ Redis", e);
        }
    }
    public void putWithExpiration(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
}
