package com.anuragNepal.ecommerce.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.anuragNepal.ecommerce.entity.Product;
import com.anuragNepal.ecommerce.repository.ProductRepository;
import com.anuragNepal.ecommerce.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService{

	private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

	@Autowired
	ProductRepository productRepository;
	
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
	public Product saveProduct(Product product) {
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Boolean deleteProduct(long id) {
		try {
			Optional<Product> product = productRepository.findById(id);
			if(product.isPresent()) {
				// Delete the product image file if it exists
				String productImage = product.get().getProductImage();
				if(productImage != null && !productImage.equals("default.jpg")) {
					try {
						Path imagePath = resolveUploadPath("product_image", productImage);
						Files.deleteIfExists(imagePath);
					} catch(Exception e) {
						logger.warn("Could not delete product image: " + productImage, e);
					}
				}
				
				productRepository.deleteById(product.get().getId());
				return true;
			} else {
				return false;
			}
		} catch(Exception e) {
			logger.error("Error deleting product with id: " + id, e);
			return false;
		}
	}

	@Override
	public Optional<Product> findById(long id) {
		return Optional.empty();
	}

	@Override
	public Product getProductById(long id) {
		return productRepository.findById(id).orElse(null);
	}

	@Override
	public Product updateProductById(Product product, MultipartFile file) {
		Product dbProductById = getProductById(product.getId());
		
		String imageName = file.isEmpty() ? dbProductById.getProductImage() : file.getOriginalFilename();
		dbProductById.setProductImage(imageName);
		dbProductById.setProductTitle(product.getProductTitle());
		dbProductById.setProductDescription(product.getProductDescription());
		dbProductById.setProductCategory(product.getProductCategory());
		dbProductById.setProductPrice(product.getProductPrice());
		dbProductById.setProductStock(product.getProductStock());
		dbProductById.setCreatedAt(product.getCreatedAt());
		dbProductById.setIsActive(product.getIsActive());
		//discount logic
		dbProductById.setDiscount(product.getDiscount());
		Double discount =product.getProductPrice()*(product.getDiscount()/100.0);
		Double discountPrice= product.getProductPrice() - discount;
		dbProductById.setDiscountPrice(discountPrice);
		
		Product updatedProduct = productRepository.save(dbProductById);
		
		//product save then we need to save our new updated image
		if(!ObjectUtils.isEmpty(updatedProduct)) {
			if(!file.isEmpty()) {
				try {
					Path productPath = resolveUploadPath("product_image");
					Files.createDirectories(productPath);
					Path path = productPath.resolve(file.getOriginalFilename());
					logger.info("File save Path :{}", path);
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					
				} catch (Exception e) {
					logger.error("Failed to save product image", e);
				}
			}
			
			return updatedProduct;
		}
		return null;
	}

	@Override
	public List<Product> findAllActiveProducts(String category) {
		List<Product> products;
		if(ObjectUtils.isEmpty(category)) {
			products = productRepository.findByIsActiveTrue();
		}else {
			products =productRepository.findByProductCategory(category);
		}
		
		return products;
	}

	@Override
	public List<Product> getLatestEightActiveProducts() {
		List<Product> products = productRepository.findByIsActiveTrueOrderByCreatedAtDesc();
		// Return only the first 8 products
		if(products.size() > 8) {
			return products.subList(0, 8);
		}
		return products;
	}

}
