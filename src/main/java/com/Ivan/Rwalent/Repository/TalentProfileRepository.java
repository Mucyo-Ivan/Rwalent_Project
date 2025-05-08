package com.Ivan.Rwalent.repository;

import com.Ivan.Rwalent.model.TalentProfile;
import com.Ivan.Rwalent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TalentProfileRepository extends JpaRepository<TalentProfile, Long> {
    // Long here is the type of TalentProfile's ID (which is User's ID)

    Optional<TalentProfile> findByUser(User user);

    Optional<TalentProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
} 