package com.crimsonlogic.eventmanagement.repository;

import com.crimsonlogic.eventmanagement.entity.Events;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Events, String> {

	List<Events> findByCreatedBy_UserDetailsId(String userDetailsId);
	
}
