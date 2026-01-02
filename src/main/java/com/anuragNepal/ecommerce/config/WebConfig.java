package com.anuragNepal.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            // Resolve the upload directory path
            Path uploadPath;
            File uploadFile = new File(uploadDir);
            
            if (uploadFile.isAbsolute()) {
                uploadPath = uploadFile.toPath();
            } else {
                // If relative, use it relative to the project directory
                uploadPath = Paths.get(uploadDir).toAbsolutePath();
            }
            
            // Create directories if they don't exist
            Files.createDirectories(uploadPath.resolve("category"));
            Files.createDirectories(uploadPath.resolve("product_image"));
            Files.createDirectories(uploadPath.resolve("profile_img"));
            
            // Serve uploaded images from the external directory
            String locationPath = uploadPath.toString().replace("\\", "/");
            if (!locationPath.endsWith("/")) {
                locationPath += "/";
            }
            
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + locationPath);
                    
            System.out.println("Upload directory configured at: " + locationPath);
            
        } catch (Exception e) {
            System.err.println("Failed to configure upload directory: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

