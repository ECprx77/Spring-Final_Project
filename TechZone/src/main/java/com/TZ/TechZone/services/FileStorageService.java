package com.TZ.TechZone.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads/products}")
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Le fichier est vide");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("Nom de fichier invalide");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!isValidImageExtension(fileExtension)) {
            throw new IOException("Type de fichier non supporté. Formats acceptés: jpg, jpeg, png, gif, webp");
        }

        String filename = UUID.randomUUID().toString() + fileExtension;
        
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    public void deleteFile(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(filename);
        Files.deleteIfExists(filePath);
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot);
    }

    private boolean isValidImageExtension(String extension) {
        String ext = extension.toLowerCase();
        return ext.equals(".jpg") || ext.equals(".jpeg") || 
               ext.equals(".png") || ext.equals(".gif") || 
               ext.equals(".webp");
    }
}
