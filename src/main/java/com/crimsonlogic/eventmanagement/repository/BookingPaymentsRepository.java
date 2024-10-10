package com.crimsonlogic.eventmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crimsonlogic.eventmanagement.entity.BookingPayments;
import com.crimsonlogic.eventmanagement.entity.Bookings;

@Repository
public interface BookingPaymentsRepository extends JpaRepository<BookingPayments, String> {

	BookingPayments findByPaymentForBooking(Bookings booking);

	@Query("SELECT SUM(bp.paymentAmount) FROM BookingPayments bp JOIN bp.paymentForBooking b WHERE b.bookingForEvent.eventId = :eventId")
	Double sumPaymentsByEventId(String eventId);
}
