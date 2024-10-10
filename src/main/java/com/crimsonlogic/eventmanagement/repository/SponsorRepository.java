package com.crimsonlogic.eventmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crimsonlogic.eventmanagement.entity.Sponsors;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsors, String>{

	List<Sponsors> findBySponsorForEvent_EventId(String eventId);

}
