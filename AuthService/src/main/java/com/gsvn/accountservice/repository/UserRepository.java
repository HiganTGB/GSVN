package com.gsvn.accountservice.repository;
import com.gsvn.accountservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);
    List<User> findAllByDeletedAtIsNull();
    Optional<User> findByEmail(String email);
    Page<User> findAllByDeletedAtIsNull(Pageable pageable);
}