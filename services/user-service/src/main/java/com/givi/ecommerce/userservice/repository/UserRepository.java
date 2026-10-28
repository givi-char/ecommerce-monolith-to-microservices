package com.givi.ecommerce.userservice.repository;

import com.givi.ecommerce.userservice.entity.User;
import com.givi.ecommerce.userservice.enums.SellerStatus;
import com.givi.ecommerce.userservice.enums.UserStatus;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndStatus(
            Long id,
            UserStatus status
    );

    List<User> findAllByStatus(
            UserStatus status
    );

    List<User> findBySellerStatus(
            SellerStatus status
    );

    long countByStatus(
            UserStatus status
    );

    long countBySellerStatus(
            SellerStatus status
    );

    @Query("""
            SELECT u
            FROM User u
            WHERE u.id = :userId
            AND u.status = :status
            """)
    @QueryHints({
            @QueryHint(
                    name = "org.hibernate.cacheable",
                    value = "false"
            )
    })
    Optional<User> findFreshByIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") UserStatus status
    );

    Page<User> findAll(
            Pageable pageable
    );

    @Query("""
            SELECT u
            FROM User u
            WHERE LOWER(u.email)
            LIKE LOWER(CONCAT('%', :email, '%'))
            """)
    Page<User> searchByEmail(
            @Param("email") String email,
            Pageable pageable
    );

    Page<User> findBySellerStatus(
            SellerStatus status,
            Pageable pageable
    );

    Page<User> findByStatus(
            UserStatus status,
            Pageable pageable
    );
}