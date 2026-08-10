package com.gsvn.accountservice.repository;
import com.gsvn.accountservice.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Set<Permission> findAllByPermissionIdIn(Set<Integer> collect);
}