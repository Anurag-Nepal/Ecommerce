package com.anuragNepal.ecommerce.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.anuragNepal.ecommerce.entity.Category;
import com.anuragNepal.ecommerce.repository.CategoryRepository;
import com.anuragNepal.ecommerce.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{

	private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Value("${app.upload.dir}")
	private String uploadDir;
	
	// Helper method to resolve upload directory path
	private Path resolveUploadPath(String... subPaths) {
		File uploadFile = new File(uploadDir);
		Path basePath;
		
		if (uploadFile.isAbsolute()) {
			basePath = uploadFile.toPath();
		} else {
			basePath = Paths.get(uploadDir).toAbsolutePath();
		}
		
		// Append sub-paths if provided
		for (String subPath : subPaths) {
			basePath = basePath.resolve(subPath);
		}
		
		return basePath;
	}
	
	@Override
	public Category saveCategory(Category category) {
		// TODO Auto-generated method stub
		return categoryRepository.save(category);
	}

	@Override
	public List<Category> getAllCategories() {
		// TODO Auto-generated method stub
		return categoryRepository.findAll();
	}

	@Override
	public boolean existCategory(String categoryName) {
		// TODO Auto-generated method stub
		return categoryRepository.existsByCategoryName(categoryName);
	}

	@Override
	public Boolean deleteCategory(long id) {
		try {
			Category categoryFound = categoryRepository.findById(id).orElse(null);
			
			if(!ObjectUtils.isEmpty(categoryFound)) {
				// Delete the category image file if it exists
				String categoryImage = categoryFound.getCategoryImage();
				if(categoryImage != null && !categoryImage.equals("default.jpg")) {
					try {
						Path imagePath = resolveUploadPath("category", categoryImage);
						Files.deleteIfExists(imagePath);
					} catch(Exception e) {
						logger.warn("Could not delete category image: " + categoryImage, e);
					}
				}
				
				categoryRepository.delete(categoryFound);
				return true;
			}
			
			return false;
		} catch(Exception e) {
			logger.error("Error deleting category with id: " + id, e);
			return false;
		}
	}

	@Override
	public Optional<Category> findById(long id) {
		// TODO Auto-generated method stub
		return categoryRepository.findById(id);
	}

	@Override
	public List<Category> findAllActiveCategory() {
		return categoryRepository.findByIsActiveTrue();
	}

}
