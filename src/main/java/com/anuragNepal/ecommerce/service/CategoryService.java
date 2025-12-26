package com.anuragNepal.ecommerce.service;

import java.util.List;
import java.util.Optional;

import com.anuragNepal.ecommerce.entity.Category;

public interface CategoryService {
	
	public Category saveCategory(Category category);
	
	boolean existCategory(String categoryName);
	
	public List<Category> getAllCategories();
	
	public Boolean deleteCategory(long id);
	
	public Optional<Category> findById(long id);
	
	List<Category> findAllActiveCategory();
	

}
