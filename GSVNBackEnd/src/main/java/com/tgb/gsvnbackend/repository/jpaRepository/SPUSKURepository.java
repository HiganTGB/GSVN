package com.tgb.gsvnbackend.repository.jpaRepository;

import com.tgb.gsvnbackend.model.entity.SPUSKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SPUSKURepository extends JpaRepository<SPUSKU,Integer> {

    List<SPUSKU> findAllBySpuId(Integer spuId);
}
