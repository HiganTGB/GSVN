package com.gsvn.customerservice.common;

import org.apache.ibatis.annotations.Param;

import java.util.Optional;

public interface IBaseMapper<T, ID> {

    int insert(T entity);

    int update(T entity);

    Optional<T> findById(@Param("id") ID id);

    int deleteById(@Param("id") ID id);
}