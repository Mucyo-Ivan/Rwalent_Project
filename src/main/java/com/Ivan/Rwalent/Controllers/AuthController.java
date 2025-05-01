package com.Ivan.Rwalent.Controllers;

import com.Ivan.Rwalent.DTO.UserDTO;
import com.Ivan.Rwalent.DTO.AuthResponse;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.Repository.UserRepository;
import com.Ivan.Rwalent.service.JWTService;
import com.Ivan.Rwalent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashSet;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserDTO userDTO) {
        logger.info("Received registration request for email: {}", userDTO.getEmail());
        logger.debug("Registration request data: {}", userDTO);

        // Check if email already exists
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            logger.warn("Registration failed: Email already exists: {}", userDTO.getEmail());
            throw new RuntimeException("Email already registered");
        }

        // Create and save the user
        User user = new User();
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        // Initialize empty roles set
        user.setRoles(new HashSet<>());
        userRepository.save(user);

        // Create and return the AuthResponse DTO with null token
        AuthResponse authResponse = new AuthResponse(userDTO.getEmail(), null, "User registered successfully");
        logger.info("User registered successfully: {}", userDTO.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserDTO userDTO) {
        logger.info("Received login request for email: {}", userDTO.getEmail());
        logger.debug("Login request data: {}", userDTO);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword())
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = userService.loadUserByUsername(userDTO.getEmail());
                String token = jwtService.generateToken(userDetails);
                logger.info("Login successful for email: {}", userDTO.getEmail());

                AuthResponse authResponse = new AuthResponse(userDTO.getEmail(), token, "Login successful");
                return ResponseEntity.ok(authResponse);
            }
        } catch (Exception e) {
            logger.warn("Login failed for email: {}: {}", userDTO.getEmail(), e.getMessage());
            throw new UsernameNotFoundException("Invalid email or password");
        }

        logger.warn("Login failed for email: {}", userDTO.getEmail());
        throw new UsernameNotFoundException("Invalid email or password");
    }
} 