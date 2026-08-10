package com.gsvn.accountservice.repository;

import com.gsvn.accountservice.model.entity.UserRole;
import com.gsvn.accountservice.model.entity.key.UserRoleKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleKey> {
    void deleteAllByUserId(String userId);


    Set<UserRole> getAllByUserId(String id);

    Set<UserRole> findByUserId(String userId);

    void deleteByUserIdAndRoleIdIn(String userId, Set<Integer> roleIdsToDelete);
}
