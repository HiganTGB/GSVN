package com.gsvn.accountservice.repository;
import com.gsvn.accountservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);
    List<User> findAllByDeletedAtIsNull();
    Optional<User> findByEmail(String email);
    Page<User> findAllByDeletedAtIsNullAndEmailContainingOrUserNameContaining(String email, String username, Pageable pageable);
    @Query(value = """
    SELECT * FROM users u 
    WHERE u.deleted_at IS NULL 
    AND (
        :keyword IS NULL 
        OR u.user_name ILIKE ('%' || :keyword || '%') 
        OR u.user_id::text ILIKE ('%' || :keyword || '%') 
        OR u.email ILIKE ('%' || :keyword || '%') 
        OR u.phone_number ILIKE ('%' || :keyword || '%')
    )
    AND (:isStaff IS NULL OR u.is_staff = :isStaff)
    ORDER BY 
        CASE WHEN :sortOrder = 'ASC' THEN
            CASE 
                WHEN :sortField = 'userName' THEN u.user_name 
                WHEN :sortField = 'email' THEN u.email
                ELSE u.user_id END
        END ASC,
        CASE WHEN :sortOrder = 'DESC' THEN
            CASE 
                WHEN :sortField = 'userName' THEN u.user_name 
                WHEN :sortField = 'email' THEN u.email
                ELSE u.user_id END
        END DESC
    """,
            countQuery = """
    SELECT count(*) FROM users u 
    WHERE u.deleted_at IS NULL 
    AND (
        :keyword IS NULL 
        OR u.user_name ILIKE ('%' || :keyword || '%') 
        OR u.user_id::text ILIKE ('%' || :keyword || '%') 
        OR u.email ILIKE ('%' || :keyword || '%') 
        OR u.phone_number ILIKE ('%' || :keyword || '%')
    )
    AND (:isStaff IS NULL OR u.is_staff = :isStaff)
    """,
            nativeQuery = true)
    Page<User> searchUsersNative(
            @Param("keyword") String keyword,
            @Param("isStaff") Boolean isStaff,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder,
            Pageable pageable
    );
}