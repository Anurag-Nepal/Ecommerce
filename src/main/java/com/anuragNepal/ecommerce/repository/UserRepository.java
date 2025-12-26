package com.anuragNepal.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anuragNepal.ecommerce.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	public User findByEmail(String email);

	public List<User> findByRole(String role);

	public User findByResetTokens(String token);
}
