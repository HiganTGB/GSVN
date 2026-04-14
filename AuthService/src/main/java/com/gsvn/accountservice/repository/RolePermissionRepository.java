package com.gsvn.accountservice.repository;

import com.gsvn.accountservice.model.entity.RolePermission;
import com.gsvn.accountservice.model.entity.key.RolePermissionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionKey> {

    void deleteAllByRoleId(Integer id);


    Set<RolePermission> getAllByRoleIdIn(Set<Integer> list);

    Set<RolePermission> getAllByRoleId(Integer id);
}
