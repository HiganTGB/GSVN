package com.tgb.gsvnbackend.repository;

import com.tgb.gsvnbackend.model.entity.SPUSKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SPUSKURepository extends JpaRepository<SPUSKU,Integer> {

    List<SPUSKU> findAllBySpuId(String spuId);
}
