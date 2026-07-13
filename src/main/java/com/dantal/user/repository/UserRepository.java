package com.dantal.user.repository;

import com.dantal.user.entity.RoleName;
import com.dantal.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.clinic.id = :clinicId AND r.name = :roleName ORDER BY u.createdAt ASC")
    List<User> findByClinicIdAndRoleName(@Param("clinicId") UUID clinicId, @Param("roleName") RoleName roleName);
}
