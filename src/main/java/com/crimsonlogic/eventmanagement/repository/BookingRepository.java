package com.crimsonlogic.eventmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crimsonlogic.eventmanagement.entity.Bookings;
import com.crimsonlogic.eventmanagement.entity.Events;

@Repository
public interface BookingRepository extends JpaRepository<Bookings, String> {

	int countByBookingForEvent(Events event);

	List<Bookings> findByBookingMadeBy_UserDetailsId(String userId);

	List<Bookings> findByBookingForEvent(Events event);

	@Query("SELECT SUM(b.noOfTickets) FROM Bookings b WHERE b.bookingForEvent.eventId = :eventId")
	Long sumTicketsByEventId(@Param("eventId") String eventId);

}
