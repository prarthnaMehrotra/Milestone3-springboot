package com.crimsonlogic.eventmanagement.repository;

import com.crimsonlogic.eventmanagement.entity.Role;
import com.crimsonlogic.eventmanagement.entity.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
	
    Role findByRoleForUser(UserAuthentication userAuthentication);
    
}
