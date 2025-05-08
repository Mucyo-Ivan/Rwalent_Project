package com.Ivan.Rwalent.config;

import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting data initialization...");
        
        // Create meddy user if it doesn't exist
        if (!userRepository.existsByEmail("meddy@gmail.com")) {
            logger.info("Creating meddy user...");
            User meddyUser = new User();
            meddyUser.setFullName("Meddy User");
            meddyUser.setEmail("meddy@gmail.com");
            meddyUser.setPassword(passwordEncoder.encode("password123"));
            meddyUser.setUserType(User.UserType.REGULAR);
            meddyUser.setPhoneNumber("+1234567890");
            meddyUser.setLocation("Test City");
            
            userRepository.save(meddyUser);
            logger.info("Meddy user created successfully");
        } else {
            logger.info("Meddy user already exists");
        }
        
        logger.info("Data initialization completed");
    }
} 