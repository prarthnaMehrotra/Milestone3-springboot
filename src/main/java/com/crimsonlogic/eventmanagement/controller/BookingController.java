package com.crimsonlogic.eventmanagement.controller;

import com.crimsonlogic.eventmanagement.service.BookingService;
import com.crimsonlogic.eventmanagement.payload.BookingDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:3001")
@Slf4j
public class BookingController {

    @Autowired
    public BookingService bookingService; // Service for handling booking logic

    /**
     * Handles ticket booking requests.
     *
     * @param userId         The ID of the user making the booking.
     * @param eventId        The ID of the event for which tickets are being booked.
     * @param ticketPriceId  The ID of the ticket price category.
     * @param numberOfTickets The number of tickets to book.
     * @return A ResponseEntity containing the created BookingDto and the HTTP status.
     */
    @PostMapping("/book")
    public ResponseEntity<BookingDto> bookTickets(@RequestParam String userId, 
                                                  @RequestParam String eventId,
                                                  @RequestParam String ticketPriceId, 
                                                  @RequestParam int numberOfTickets) {
        try {
            // Call the service method to book tickets and capture the created BookingDto
            BookingDto createdBookingDto = bookingService.bookTickets(userId, eventId, ticketPriceId, numberOfTickets);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBookingDto); // Return the created booking with 201 status
        } catch (RuntimeException e) {
            // Log the error for debugging
            log.error("Error booking tickets: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Return a 400 status on error
        }
    }

    /**
     * Retrieves all bookings for a specific user.
     *
     * @param userId The ID of the user whose bookings are to be retrieved.
     * @return A ResponseEntity containing a list of BookingDto objects.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDto>> getUserBookings(@PathVariable String userId) {
        // Fetch and return the user's bookings
        List<BookingDto> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings); // Return the bookings with a 200 status
    }

    /**
     * Cancels a booking by its ID.
     *
     * @param bookingId The ID of the booking to cancel.
     * @return A ResponseEntity with a success message.
     */
    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable String bookingId) {
        bookingService.cancelBooking(bookingId); // Call the service method to cancel the booking
        return ResponseEntity.ok("Booking cancelled successfully."); // Return success message with a 200 status
    }

    /**
     * Retrieves total revenue and ticket count for a specific event.
     *
     * @param eventId The ID of the event for which to fetch revenue information.
     * @return A ResponseEntity containing revenue information and the HTTP status.
     */
    @GetMapping("/revenue/{eventId}")
    public ResponseEntity<Map<String, Object>> getTotalRevenueForEvent(@PathVariable String eventId) {
        try {
            // Fetch revenue information for the specified event
            Map<String, Object> revenueInfo = bookingService.getTotalRevenueAndTicketsForEvent(eventId);
            return ResponseEntity.ok(revenueInfo); // Return the revenue info with a 200 status
        } catch (RuntimeException e) {
            log.error("Error fetching revenue for event {}: {}", eventId, e.getMessage()); // Log the error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Return a 400 status on error
        }
    }

}
