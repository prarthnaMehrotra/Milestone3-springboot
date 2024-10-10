package com.crimsonlogic.eventmanagement.repository;

import com.crimsonlogic.eventmanagement.entity.UserAuthentication;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, String> {
	
	UserAuthentication findByEmail(String email);
	
	Optional<UserAuthentication> findByUserId(String userId);
	
}
