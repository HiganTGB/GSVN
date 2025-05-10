package com.tgb.gsvnbackend.repository;

import com.tgb.gsvnbackend.model.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SKURepository extends JpaRepository<SKU,Integer> {

}
