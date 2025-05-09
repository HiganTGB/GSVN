package com.tgb.gsvnbackend.repository;

import com.tgb.gsvnbackend.model.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand,Integer>, QuerydslPredicateExecutor<Brand> {
    List<Brand> findByTitleContaining(String title);
}
