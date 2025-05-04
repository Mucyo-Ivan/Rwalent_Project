package com.Ivan.Rwalent.repository;

import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.model.User.TalentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.userType = 'TALENT' " +
           "AND (:searchTerm IS NULL OR " +
           "    LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "    LOWER(u.bio) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "    LOWER(u.serviceAndPricing) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "    LOWER(u.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "    LOWER(u.category) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:category IS NULL OR u.category = :category) " +
           "AND (:location IS NULL OR LOWER(u.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    Page<User> searchTalents(
            @Param("searchTerm") String searchTerm,
            @Param("category") TalentCategory category,
            @Param("location") String location,
            Pageable pageable
    );
} 