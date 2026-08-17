package com.gsvn.hrmservice.mapper;

import com.gsvn.hrmservice.model.entity.Branch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BranchMapper {

    int insert(Branch branch);

    int update(Branch branch);

    List<Branch> findAll();

    Optional<Branch> findById(@Param("id") Long id);

    boolean existByCode(@Param("code") String code);

    int deleteById(@Param("id") Long id);

    List<Branch> findAdvanced(@Param("keyword") String keyword,
                              @Param("sortBy") String sortBy,
                              @Param("direction") String direction,
                              @Param("size") int size,
                              @Param("offset") long offset);

    long countAdvanced(@Param("keyword") String keyword);
}