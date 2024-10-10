package com.crimsonlogic.eventmanagement.repository;

import com.crimsonlogic.eventmanagement.entity.EventCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategories, String> {
	
}
