
package com.smartacademic.repository;

import com.smartacademic.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByUsernameIgnoreCaseOrEmailIgnoreCase(
            String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
        SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        FROM UserEntity u JOIN u.roles r
        WHERE r.name = :roleName
        """)
    boolean existsByRoleName(@Param("roleName") String roleName);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCaseAndIdNot(
            String username, Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email, Long id);
    }
