package com.tgb.gsvnbackend.repository.jpaRepository;

import com.tgb.gsvnbackend.model.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SKURepository extends JpaRepository<SKU,Integer> {

}
