package com.crimsonlogic.eventmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crimsonlogic.eventmanagement.entity.Events;
import com.crimsonlogic.eventmanagement.entity.Venue;

@Repository
public interface VenueRepository extends JpaRepository<Venue, String> {

	Venue findByVenueForEvent(Events event);
}
