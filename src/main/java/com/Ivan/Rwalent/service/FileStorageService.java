package com.Ivan.Rwalent.service;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
 
public interface FileStorageService {
    String storeFile(MultipartFile file, String userId);
    Path getFilePath(String fileName);
    void deleteFile(String fileName);
} 