package com.Ivan.Rwalent.service;

import com.Ivan.Rwalent.dto.LoginDTO;
import com.Ivan.Rwalent.dto.TalentSearchDTO;
import com.Ivan.Rwalent.dto.UserRegistrationDTO;
import com.Ivan.Rwalent.model.User;
import org.springframework.data.domain.Page;

public interface UserService {
    User registerUser(UserRegistrationDTO registrationDTO);
    User findUserByEmail(String email);
    boolean existsByEmail(String email);
    Page<User> searchTalents(TalentSearchDTO searchDTO);
    User updateUser(User user);
    User getUserById(Long id);
} 