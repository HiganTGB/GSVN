package com.gsvn.accountservice.repository;

import com.gsvn.accountservice.model.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(String roleName);
    Set<Role> findAllByRoleIdIn(Set<Integer> userRoles);

    boolean existsRoleByRoleName(String roleName);
    boolean existsByRoleNameAndRoleIdNot(String roleName, int roleId);
    Page<Role> findByRoleNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String roleName, String description, Pageable pageable);
}