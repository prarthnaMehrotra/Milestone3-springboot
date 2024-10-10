package com.crimsonlogic.eventmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crimsonlogic.eventmanagement.entity.TicketPrice;

@Repository
public interface TicketPriceRepository extends JpaRepository<TicketPrice, String> {

	List<TicketPrice> findByTicketForEvent_EventId(String eventId);

}
