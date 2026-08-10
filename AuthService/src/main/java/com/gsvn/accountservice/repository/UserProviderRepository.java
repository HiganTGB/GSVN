package com.gsvn.accountservice.repository;


import com.gsvn.accountservice.model.entity.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProviderRepository extends JpaRepository<UserProvider, Long> {
    Optional<UserProvider> findByProviderNameAndProviderUserId(String providerName, String providerUserId);
    Optional<UserProvider> findByUserIdAndProviderName(String userId, String providerName);
}