package com.tgb.gsvnbackend.repository;

import com.tgb.gsvnbackend.model.entity.Brand;
import com.tgb.gsvnbackend.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer>, QuerydslPredicateExecutor<Category> {
    List<Category> findByTitleContaining(String title);
}
