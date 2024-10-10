package com.crimsonlogic.eventmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crimsonlogic.eventmanagement.entity.UserDetails;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, String> {

	UserDetails findByDetailsOfUser_UserId(String userId);
}
