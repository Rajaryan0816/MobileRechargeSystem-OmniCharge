package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByEmail(String mail);
	Optional<User> findByPhoneNumber(String phoneNumber);
	Optional<User> findByUserName(String userName);
	Optional<User> findByEmailOrUserName(String phone,String userName);
	
}
