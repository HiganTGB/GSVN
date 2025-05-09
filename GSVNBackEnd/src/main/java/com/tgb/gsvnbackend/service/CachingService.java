package com.tgb.gsvnbackend.service;

import java.util.List;
import java.util.Map;

public interface CachingService {


    <T> void setPageData(String key, int page, int size, List<T> data, Class<T> clazz);


    <T> List<T> getPageData(String key, int page, int size, Class<T> clazz);


    <T> void saveById(String key,  Integer id, T data, Class<T> clazz);


    <T> T getById(String key,Integer id, Class<T> clazz);


    void deleteById(String key, Integer id);

    <T> void saveListBySearchValue(String key, String searchValue, List<T> data, Class<T> clazz);
    <T> List<T> getListBySearchValue(String key, String searchValue, Class<T> clazz);
}

