package com.anuragNepal.ecommerce.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.anuragNepal.ecommerce.entity.Category;
import com.anuragNepal.ecommerce.entity.Product;
import com.anuragNepal.ecommerce.entity.User;
import com.anuragNepal.ecommerce.entity.Order;
import com.anuragNepal.ecommerce.service.CartService;
import com.anuragNepal.ecommerce.service.CategoryService;
import com.anuragNepal.ecommerce.service.ProductService;
import com.anuragNepal.ecommerce.service.UserService;
import com.anuragNepal.ecommerce.service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminViewController {
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductService productService;

	@Autowired
	UserService userService;
	
	@Autowired
	CartService cartService;
	
	@Autowired
	OrderService orderService;
	
	@Value("${app.upload.dir}")
	private String uploadDir;
	
	//to track which user is login right Now
	//by default call this method when any request come to this controller because of @ModelAttribut
	@ModelAttribute 
	public void getUserDetails(Principal principal, Model model) {
		if(principal != null) {
			String currenLoggedInUserEmail = principal.getName();
			User currentUserDetails = userService.getUserByEmail(currenLoggedInUserEmail);
			//System.out.println("Current Logged In User is :: ADMIN Controller :: "+currentUserDetails.toString());
			model.addAttribute("currentLoggedInUserDetails",currentUserDetails);
			
			//for showing user cart count
			Long countCartForUser = cartService.getCounterCart(currentUserDetails.getId());
			System.out.println("Admin Cart Count :"+countCartForUser);
			model.addAttribute("countCartForUser", countCartForUser);
			
		}
		List<Category> allActiveCategory = categoryService.findAllActiveCategory();
		model.addAttribute("allActiveCategory",allActiveCategory);
		
	}
	
	@GetMapping("/")
	public String adminIndex() {
		
		return "admin/admin-dashboard";
	}
	
	
	//CATEGORY-MODULE-START
	
	@GetMapping("/add-category")
	public String addCategory(Model model) {
		
		return "admin/category/category-add-form";
	}
	
	@PostMapping("/save-category")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
		
		String imageName = file !=null ? file.getOriginalFilename() : "default.jpg";
		category.setCategoryImage(imageName);
		
		if(categoryService.existCategory(category.getCategoryName())) {
			session.setAttribute("errorMsg", "Category Name already Exists");
		}else {
			Category saveCategory = categoryService.saveCategory(category);
			
			if(ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not Saved! Internal Server Error!");
			}else {
				// save file to external dir
				try {
					Path categoryPath = Paths.get(uploadDir, "category");
					Files.createDirectories(categoryPath);
					Path path = categoryPath.resolve(file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					//set Suceesss Msg to Session
					session.setAttribute("successMsg", "Category Save Successfully.");
				} catch (IOException ex) {
					ex.printStackTrace();
					session.setAttribute("errorMsg", "Failed to save category image: " + ex.getMessage());
				}
				
			}
			
		}
		
		
		return "redirect:/admin/category";
	}

	@GetMapping("/category")
	public String category(Model model) {
		System.out.println("category:WWWWWWWWW");
		List<Category> allCategories = categoryService.getAllCategories();
		System.out.println("category: "+allCategories.toString());
		for (Category category : allCategories) {
			//category.getCreatedAt();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm:ss");
			String format = formatter.format(category.getCreatedAt());
			model.addAttribute("formattedDateTimeCreatedAt",format);
			
		}
		
		model.addAttribute("allCategoryList",allCategories);
		
		return "/admin/category/category-home";
	}
	
	
	@GetMapping("/edit-category/{id}")
	public String editCategoryForm(@PathVariable("id") long id, Model model) {
		//System.out.println("ID :"+id);
		Optional<Category> categoryObj = categoryService.findById(id);
		if(categoryObj.isPresent()) {
			Category category = categoryObj.get();
			model.addAttribute("category", category);
		}else {
			System.out.println("ELSEEEEE");
		}
		return "/admin/category/category-edit-form";
	}
	
	
	@PostMapping("/update-category")
	public String udateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
		System.out.println("Category for UPDATE :"+category.toString());
		
		Optional<Category> categoryById = categoryService.findById(category.getId());
		System.out.println("Category obj"+categoryById.toString());
		
		
		if(categoryById.isPresent()) {
			System.out.println("Present:");
			Category oldCategory = categoryById.get();
			System.out.println("Category old Obj "+oldCategory.toString());
			oldCategory.setCategoryName(category.getCategoryName());
			oldCategory.setIsActive(category.getIsActive());
			//oldCategory.setUpdatedAt(LocalDateTime.now());
			
			
			String imageName =  file.isEmpty() ?  oldCategory.getCategoryImage() : file.getOriginalFilename();
			oldCategory.setCategoryImage(imageName);	
			
			Category updatedCategory = categoryService.saveCategory(oldCategory);
			
			if(!ObjectUtils.isEmpty(updatedCategory)) {
				//save File
				if(!file.isEmpty()) {
					// save file to external dir
					Path categoryPath = Paths.get(uploadDir, "category");
					Files.createDirectories(categoryPath);
					Path path = categoryPath.resolve(file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				
				session.setAttribute("successMsg", "Category Updated Successfully");
			}else {
				session.setAttribute("errorMsg", "Something wrong on server!");
			}
			
			
			
			//OR
//			if(file!=null) {
//				String newImageName = file.getOriginalFilename();
//				System.out.println("File name: "+newImageName);
//				oldCategory.setCategoryImage(newImageName);
//			}else {
//				String oldOriginalImg = oldCategory.getCategoryImage();
//				System.out.println("File name ELSE: "+oldOriginalImg);
//				oldCategory.setCategoryImage(oldOriginalImg);
//			}
			
			
		}else {
			System.out.println("Not Present:");
		}
		
		return "redirect:/admin/category";
	}
	
	@GetMapping("/delete-category/{id}")
	public String deleteCategory(@PathVariable("id") long id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);
		if(deleteCategory) {
			session.setAttribute("successMsg", "Category Deleted Successfully");
		}else {
			session.setAttribute("errorMsg", "Server Error");
		}
		
		return "redirect:/admin/category";
	}
	
	
	//PRODUCT-MODULE-START
	
	@GetMapping("/add-product")
	public String addProduct(Model model) {
		List<Category> allCategories = categoryService.getAllCategories();
		model.addAttribute("allCategoryList",allCategories);
		return "/admin/product/add-product";
	}
	

	
	@PostMapping("/save-product")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
		String imageName = file !=null ? file.getOriginalFilename() : "default.png"; 
		
		product.setProductImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getProductPrice());
		
		Product saveProduct = productService.saveProduct(product);
		 
		if(!ObjectUtils.isEmpty(saveProduct)) {
			try {
				Path productPath = Paths.get(uploadDir, "product_image");
				Files.createDirectories(productPath);
				Path path = productPath.resolve(imageName);
				System.out.println("File save Path :"+path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				session.setAttribute("successMsg", "Product Save Successfully.");
			} catch (IOException ex) {
				ex.printStackTrace();
				session.setAttribute("errorMsg", "Failed to save product image: " + ex.getMessage());
			}
		}else {
			session.setAttribute("errorMsg", "Something Wrong on server while save Product");
			//System.out.println("Something Wrong on server while save Product");
		}
		
		return "redirect:/admin/product-list";
	}
	
	@GetMapping("/product-list")
	public String productList(Model model) {
		model.addAttribute("productList", productService.getAllProducts());
		return "/admin/product/product-list";
	}
	
	@GetMapping("/delete-product/{id}")
	public String deleteProduct(@PathVariable("id") long id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		
		if(deleteProduct) {
			session.setAttribute("successMsg", "Product Deleted Successfully.");
		}else {
			session.setAttribute("errorMsg", "Something Wrong on server while deleting Product");
		}
		return "redirect:/admin/product-list";
		
	}
	
	@GetMapping("/edit-product/{id}")
	public String editProduct(@PathVariable long id,Model model) {
		Product product = productService.getProductById(id);
		model.addAttribute("product",product);
		model.addAttribute("allCategoryList",categoryService.getAllCategories());
		return "/admin/product/edit-product";
	}
	
	@PostMapping("/update-product")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file,
			HttpSession session, Model model) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "INVALID DISCOUNT!");
		} else {
			Product updateProduct = productService.updateProductById(product, file);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("successMsg", "Product Updated Successfully.");
				// if file provided, save to external dir
				if (!file.isEmpty()) {
					try {
						Path productPath = Paths.get(uploadDir, "product_image");
						Files.createDirectories(productPath);
						Path path = productPath.resolve(file.getOriginalFilename());
						Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException ex) {
						ex.printStackTrace();
						session.setAttribute("errorMsg", "Failed to save updated product image: " + ex.getMessage());
					}
				}
			} else {
				session.setAttribute("errorMsg", "Something Wrong on server while deleting Product");
			}
		}

		// return "redirect:/admin/product/edit-product";
		return "redirect:/admin/product-list";
	}
	
	
	
	//USER-WORK
	//get all users
	@GetMapping("/get-all-users")
	public String getAllUser(Model model) {
		
		List<User> allUsers = userService.getAllUsersByRole("ROLE_USER");
		for (User user : allUsers) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String format = formatter.format(user.getCreatedAt());
			model.addAttribute("formattedDateTimeCreatedAt",format);
			
		}
		model.addAttribute("allUsers",allUsers);
		
		return "/admin/users/user-home";
		
	}
	

	@GetMapping("/edit-user-status")
	public String editUser(@RequestParam("status") Boolean status, @RequestParam("id") Long id, Model model, HttpSession session) {
		Boolean updateUserStatus = userService.updateUserStatus(status,id);
		if(updateUserStatus == true) {
			session.setAttribute("successMsg", "User Status Updated Successfully.");
		}
		else {
			session.setAttribute("errorMsg", "Something Wrong on server while Updating User status");
		}
		return "redirect:/admin/get-all-users";
		
	}
	
	//ORDER-MANAGEMENT
	@GetMapping("/manage-orders")
	public String manageOrders(Model model) {
		List<Order> allOrders = orderService.getAllOrders();
		model.addAttribute("allOrders", allOrders);
		return "/admin/orders/manage-orders";
	}
	
	@GetMapping("/order-details/{id}")
	public String orderDetails(@PathVariable Long id, Model model, HttpSession session) {
		Order order = orderService.getOrderById(id);
		if(order != null) {
			model.addAttribute("order", order);
			return "/admin/orders/order-details";
		} else {
			session.setAttribute("errorMsg", "Order not found");
			return "redirect:/admin/manage-orders";
		}
	}
	
	@GetMapping("/update-order-status/{id}")
	public String updateOrderStatusPage(@PathVariable Long id, Model model, HttpSession session) {
		Order order = orderService.getOrderById(id);
		if(order != null) {
			model.addAttribute("order", order);
			return "/admin/orders/update-order-status";
		} else {
			session.setAttribute("errorMsg", "Order not found");
			return "redirect:/admin/manage-orders";
		}
	}
	
	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam("orderId") Long orderId,
	                                 @RequestParam("orderStatus") String orderStatus,
	                                 HttpSession session) {
		Boolean updated = orderService.updateOrderStatus(orderId, orderStatus);
		if(updated) {
			session.setAttribute("successMsg", "Order status updated successfully");
		} else {
			session.setAttribute("errorMsg", "Failed to update order status");
		}
		return "redirect:/admin/manage-orders";
	}
	
	//ADMIN-MANAGEMENT
	@GetMapping("/manage-admins")
	public String manageAdmins(Model model) {
		List<User> allAdmins = userService.getAllUsersByRole("ROLE_ADMIN");
		for (User admin : allAdmins) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String format = formatter.format(admin.getCreatedAt());
			model.addAttribute("formattedDateTimeCreatedAt", format);
		}
		model.addAttribute("allAdmins", allAdmins);
		return "/admin/admins/admin-home";
	}
	
	@GetMapping("/edit-admin-status")
	public String editAdminStatus(@RequestParam("status") Boolean status, @RequestParam("id") Long id, Model model, HttpSession session) {
		Boolean updateAdminStatus = userService.updateUserStatus(status, id);
		if(updateAdminStatus) {
			session.setAttribute("successMsg", "Admin Status Updated Successfully.");
		} else {
			session.setAttribute("errorMsg", "Something went wrong while updating admin status");
		}
		return "redirect:/admin/manage-admins";
	}
	
	@GetMapping("/add-admin")
	public String addAdmin(Model model) {
		return "/admin/admins/add-admin";
	}
	
	@PostMapping("/save-admin")
	public String saveAdmin(@ModelAttribute User admin, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
		String original = file.getOriginalFilename();
		String safeFileName = (original == null || original.isBlank()) ? "default.jpg" : Paths.get(original).getFileName().toString();
		String profileImage = file.isEmpty() ? "default.jpg" : safeFileName;
		admin.setProfileImage(profileImage);
		admin.setRole("ROLE_ADMIN");
		
		User saveAdmin = userService.saveUser(admin);
		
		if(!ObjectUtils.isEmpty(saveAdmin)) {
			if(!file.isEmpty()) {
				Path adminProfilePath = Paths.get(uploadDir, "profile_img");
				Files.createDirectories(adminProfilePath);
				Path path = adminProfilePath.resolve(safeFileName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("successMsg", "Admin Added Successfully");
		} else {
			session.setAttribute("errorMsg", "Failed to add admin");
		}
		return "redirect:/admin/manage-admins";
	}
	
	@GetMapping("/edit-admin/{id}")
	public String editAdmin(@PathVariable Long id, Model model) {
		User admin = userService.getUserById(id);
		if(admin != null) {
			model.addAttribute("admin", admin);
			return "/admin/admins/edit-admin";
		}
		return "redirect:/admin/manage-admins";
	}
	
	@PostMapping("/update-admin")
	public String updateAdmin(@ModelAttribute User admin, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
		User existingAdmin = userService.getUserById(admin.getId());
		if(existingAdmin != null) {
			existingAdmin.setName(admin.getName());
			existingAdmin.setEmail(admin.getEmail());
			existingAdmin.setMobile(admin.getMobile());
			existingAdmin.setCity(admin.getCity());
			existingAdmin.setState(admin.getState());
			
			String profileImage = existingAdmin.getProfileImage();
			if(!file.isEmpty()) {
				String original = file.getOriginalFilename();
				String safeFileName = (original == null || original.isBlank()) ? "default.jpg" : Paths.get(original).getFileName().toString();
				profileImage = safeFileName;
				existingAdmin.setProfileImage(profileImage);
				
				Path adminProfilePath = Paths.get(uploadDir, "profile_img");
				Files.createDirectories(adminProfilePath);
				Path path = adminProfilePath.resolve(safeFileName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			
			User updatedAdmin = userService.updateUser(existingAdmin);
			if(updatedAdmin != null) {
				session.setAttribute("successMsg", "Admin Updated Successfully");
			} else {
				session.setAttribute("errorMsg", "Failed to update admin");
			}
		}
		return "redirect:/admin/manage-admins";
	}
	

}
