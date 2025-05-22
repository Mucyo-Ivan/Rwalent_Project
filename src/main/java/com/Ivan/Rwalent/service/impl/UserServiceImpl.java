package com.Ivan.Rwalent.service.impl;

import com.Ivan.Rwalent.dto.LoginDTO;
import com.Ivan.Rwalent.dto.TalentSearchDTO;
import com.Ivan.Rwalent.dto.UserRegistrationDTO;
import com.Ivan.Rwalent.exception.UserNotFoundException;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.repository.UserRepository;
import com.Ivan.Rwalent.service.FileStorageService;
import com.Ivan.Rwalent.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service("userService")
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public User registerUser(UserRegistrationDTO registrationDTO) {
        logger.info("Attempting to register user with email: {}", registrationDTO.getEmail());
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            logger.warn("Registration failed: Email already exists: {}", registrationDTO.getEmail());
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullName(registrationDTO.getFullName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setUserType(registrationDTO.getUserType());

        // Handle profile picture upload
        if (registrationDTO.getProfilePicture() != null && !registrationDTO.getProfilePicture().isEmpty()) {
            String fileName = fileStorageService.storeFile(registrationDTO.getProfilePicture(), user.getEmail());
            user.setProfilePicture(fileName);
        }

        // If user is a talent, set additional fields
        if (registrationDTO.getUserType() == User.UserType.TALENT) {
            logger.info("Registering talent user with additional fields");
            user.setPhoneNumber(registrationDTO.getPhoneNumber());
            user.setCategory(registrationDTO.getCategory());
            user.setLocation(registrationDTO.getLocation());
            user.setBio(registrationDTO.getBio());
            user.setServiceAndPricing(registrationDTO.getServiceAndPricing());
            user.setPhotoUrl(registrationDTO.getPhotoUrl());
        }

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getEmail());
        return savedUser;
    }

    @Override
    public User findUserByEmail(String email) {
        logger.info("Attempting to find user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public boolean existsByEmail(String email) {
        logger.info("Checking if user exists with email: {}", email);
        boolean exists = userRepository.existsByEmail(email);
        logger.info("User exists check result for email {}: {}", email, exists);
        return exists;
    }

    @Override
    public Page<User> searchTalents(TalentSearchDTO searchDTO) {
        logger.info("Searching talents with criteria: {}", searchDTO);
        Sort.Direction direction = Sort.Direction.fromString(searchDTO.getSortDirection());
        Sort sort = Sort.by(direction, searchDTO.getSortBy());
        PageRequest pageRequest = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), sort);

        Page<User> results = userRepository.searchTalents(
            searchDTO.getSearchTerm(),
            searchDTO.getCategory(),
            searchDTO.getLocation(),
            pageRequest
        );
        logger.info("Found {} talents matching search criteria", results.getTotalElements());
        return results;
    }

    @Override
    public User getUserById(Long id) {
        logger.info("Getting user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow((Supplier<UserNotFoundException>) () -> 
                    new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public User updateUser(User user) {
        logger.info("Updating user with id: {}", user.getId());
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException("User not found with id: " + user.getId());
        }
        return userRepository.save(user);
    }
} 