package com.gsvn.hrmservice.mapper;

import com.gsvn.hrmservice.model.entity.Position;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PositionMapper {
    int insert(Position position);
    int update(Position position);
    List<Position> findAll();
    Optional<Position> findById(@Param("id") Integer id);
    int deleteById(@Param("id") Integer id);

    List<Position> findAdvanced(
            @Param("keyword") String keyword,
            @Param("sortBy") String sortBy,
            @Param("direction") String direction,
            @Param("offset") int offset,
            @Param("size") int size
    );
    boolean existByName(@Param("name") String name);

    long countAdvanced(@Param("keyword") String keyword);
}