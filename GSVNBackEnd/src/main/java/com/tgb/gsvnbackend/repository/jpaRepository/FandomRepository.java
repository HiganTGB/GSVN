package com.tgb.gsvnbackend.repository.jpaRepository;

import com.tgb.gsvnbackend.model.entity.Fandom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FandomRepository extends JpaRepository<Fandom,Integer>, QuerydslPredicateExecutor<Fandom> {
    List<Fandom> findByTitleContaining(String title);
}
