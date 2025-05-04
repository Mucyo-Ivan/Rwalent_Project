package com.Ivan.Rwalent.service.impl;

import com.Ivan.Rwalent.dto.LoginDTO;
import com.Ivan.Rwalent.dto.TalentSearchDTO;
import com.Ivan.Rwalent.dto.UserRegistrationDTO;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.repository.UserRepository;
import com.Ivan.Rwalent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserRegistrationDTO registrationDTO) {
        if (existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullName(registrationDTO.getFullName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setUserType(registrationDTO.getUserType());

        // If user is a talent, set additional fields
        if (registrationDTO.getUserType() == User.UserType.TALENT) {
            user.setPhoneNumber(registrationDTO.getPhoneNumber());
            user.setCategory(registrationDTO.getCategory());
            user.setLocation(registrationDTO.getLocation());
            user.setBio(registrationDTO.getBio());
            user.setServiceAndPricing(registrationDTO.getServiceAndPricing());
            user.setPhotoUrl(registrationDTO.getPhotoUrl());
        }

        return userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Page<User> searchTalents(TalentSearchDTO searchDTO) {
        Sort.Direction direction = Sort.Direction.fromString(searchDTO.getSortDirection());
        Sort sort = Sort.by(direction, searchDTO.getSortBy());
        PageRequest pageRequest = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), sort);

        return userRepository.searchTalents(
            searchDTO.getSearchTerm(),
            searchDTO.getCategory(),
            searchDTO.getLocation(),
            pageRequest
        );
    }
} 