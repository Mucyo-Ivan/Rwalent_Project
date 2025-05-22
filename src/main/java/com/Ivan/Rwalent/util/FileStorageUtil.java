package com.Ivan.Rwalent.util;

import com.Ivan.Rwalent.exception.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageUtil.class);
    private static final String DEFAULT_AVATAR_PATH = "static/default-avatar.png";

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        // Validate file
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }

        // Check file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new FileStorageException("File size exceeds 5MB limit");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new FileStorageException("Only JPG and PNG files are allowed");
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;

            // Copy file to target location
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + file.getOriginalFilename() + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            // Check if fileName is actually a URL path (from legacy data)
            if (fileName.startsWith("/")) {
                logger.warn("Attempted to load file with URL path: {}. Returning default avatar.", fileName);
                return getDefaultAvatar();
            }

            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                logger.warn("File not found: {}. Returning default avatar.", fileName);
                return getDefaultAvatar();
            }
        } catch (MalformedURLException ex) {
            logger.error("Error loading file: {}", fileName, ex);
            return getDefaultAvatar();
        }
    }

    /**
     * Returns a default avatar image when no profile picture is found
     */
    private Resource getDefaultAvatar() {
        try {
            // Try to get the default avatar from classpath resources
            Resource defaultAvatar = new ClassPathResource(DEFAULT_AVATAR_PATH);
            if (defaultAvatar.exists()) {
                return defaultAvatar;
            }

            // If the default avatar doesn't exist in classpath, create one in the upload directory
            Path defaultAvatarPath = Paths.get(uploadDir).resolve("default-avatar.png");
            if (!Files.exists(defaultAvatarPath)) {
                // Create a simple default avatar (this is just a fallback if the classpath resource doesn't exist)
                // You could replace this with actual code to create a basic avatar image
                Files.createDirectories(defaultAvatarPath.getParent());
                Files.write(defaultAvatarPath, new byte[0]);
            }

            return new UrlResource(defaultAvatarPath.toUri());
        } catch (Exception e) {
            logger.error("Error creating default avatar", e);
            throw new FileStorageException("Could not create default avatar", e);
        }
    }
}