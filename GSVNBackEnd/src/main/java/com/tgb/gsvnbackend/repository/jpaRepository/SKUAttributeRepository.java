package com.tgb.gsvnbackend.repository.jpaRepository;

import com.tgb.gsvnbackend.model.entity.SKUAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SKUAttributeRepository extends JpaRepository<SKUAttribute,Integer> {
    List<SKUAttribute> findSKUAttributeByAttrsLike(Map<String, Object> attrs);
}
