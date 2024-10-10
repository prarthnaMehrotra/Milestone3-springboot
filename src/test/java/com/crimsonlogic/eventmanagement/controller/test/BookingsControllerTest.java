package com.crimsonlogic.eventmanagement.controller.test;

import com.crimsonlogic.eventmanagement.controller.BookingController;
import com.crimsonlogic.eventmanagement.payload.BookingDto;
import com.crimsonlogic.eventmanagement.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingsControllerTest {

    private BookingService bookingService;
    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        bookingService = Mockito.mock(BookingService.class);
        bookingController = new BookingController();
        bookingController.bookingService = bookingService; 
    }

    @Test
    void testBookTickets() {
        String userId = "user-1";
        String eventId = "event-1";
        String ticketPriceId = "ticket-1";
        int numberOfTickets = 2;

        BookingDto bookingDto = new BookingDto();
        when(bookingService.bookTickets(userId, eventId, ticketPriceId, numberOfTickets)).thenReturn(bookingDto);

        ResponseEntity<BookingDto> response = bookingController.bookTickets(userId, eventId, ticketPriceId, numberOfTickets);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(bookingDto, response.getBody());
        verify(bookingService).bookTickets(userId, eventId, ticketPriceId, numberOfTickets);
    }

    @Test
    void testBookTickets_Error() {
        String userId = "user-1";
        String eventId = "event-1";
        String ticketPriceId = "ticket-1";
        int numberOfTickets = 2;

        when(bookingService.bookTickets(anyString(), anyString(), anyString(), anyInt())).thenThrow(new RuntimeException("Booking failed"));

        ResponseEntity<BookingDto> response = bookingController.bookTickets(userId, eventId, ticketPriceId, numberOfTickets);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(bookingService).bookTickets(userId, eventId, ticketPriceId, numberOfTickets);
    }

    @Test
    void testGetUserBookings() {
        String userId = "user-1";
        List<BookingDto> bookings = Collections.singletonList(new BookingDto());
        when(bookingService.getUserBookings(userId)).thenReturn(bookings);

        ResponseEntity<List<BookingDto>> response = bookingController.getUserBookings(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
        verify(bookingService).getUserBookings(userId);
    }

    @Test
    void testCancelBooking() {
        String bookingId = "booking-1";

        ResponseEntity<String> response = bookingController.cancelBooking(bookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Booking cancelled successfully.", response.getBody());
        verify(bookingService).cancelBooking(bookingId);
    }

    @Test
    void testGetTotalRevenueForEvent() {
        String eventId = "event-1";
        Map<String, Object> revenueInfo = Map.of("totalRevenue", 1000, "totalTickets", 50);
        when(bookingService.getTotalRevenueAndTicketsForEvent(eventId)).thenReturn(revenueInfo);

        ResponseEntity<Map<String, Object>> response = bookingController.getTotalRevenueForEvent(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(revenueInfo, response.getBody());
        verify(bookingService).getTotalRevenueAndTicketsForEvent(eventId);
    }

    @Test
    void testGetTotalRevenueForEvent_Error() {
        String eventId = "event-1";
        when(bookingService.getTotalRevenueAndTicketsForEvent(eventId)).thenThrow(new RuntimeException("Error fetching revenue"));

        ResponseEntity<Map<String, Object>> response = bookingController.getTotalRevenueForEvent(eventId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(bookingService).getTotalRevenueAndTicketsForEvent(eventId);
    }
}
