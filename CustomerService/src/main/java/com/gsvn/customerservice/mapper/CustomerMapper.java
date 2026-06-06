package com.gsvn.customerservice.mapper;

import com.gsvn.customerservice.common.IBaseMapper;
import com.gsvn.customerservice.model.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CustomerMapper extends IBaseMapper<Customer,Long> {


    Optional<Customer> findByUserId(@Param("userId") String userId);

    Optional<Customer> findByPhone(@Param("phoneNumber") String phoneNumber);
    Optional<Customer> findByEmail(@Param("email") String email);


    List<Customer> findAdvanced(@Param("kw") String kw,
                                @Param("sortField") String sortField,
                                @Param("sortOrder") String sortOrder,
                                   @Param("size") int size,
                                   @Param("offset") int offset);

    long countAdvanced(@Param("kw") String kw);

    int deleteById(@Param("customerId") Long customerId);
}