package com.tgb.gsvnbackend.repository;

import com.tgb.gsvnbackend.model.entity.SKUAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SKUAttributeRepository extends JpaRepository<SKUAttribute,Integer> {
}
