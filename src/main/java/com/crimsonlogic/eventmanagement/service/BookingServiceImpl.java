package com.crimsonlogic.eventmanagement.service;

import com.crimsonlogic.eventmanagement.entity.Bookings;
import com.crimsonlogic.eventmanagement.entity.BookingPayments;
import com.crimsonlogic.eventmanagement.entity.Events;
import com.crimsonlogic.eventmanagement.entity.TicketPrice;
import com.crimsonlogic.eventmanagement.entity.Venue;
import com.crimsonlogic.eventmanagement.entity.Wallet;
import com.crimsonlogic.eventmanagement.entity.UserDetails;
import com.crimsonlogic.eventmanagement.payload.BookingDto;
import com.crimsonlogic.eventmanagement.repository.BookingPaymentsRepository;
import com.crimsonlogic.eventmanagement.repository.BookingRepository;
import com.crimsonlogic.eventmanagement.repository.EventRepository;
import com.crimsonlogic.eventmanagement.repository.TicketPriceRepository;
import com.crimsonlogic.eventmanagement.repository.VenueRepository;
import com.crimsonlogic.eventmanagement.repository.WalletRepository;
import com.crimsonlogic.eventmanagement.repository.UserDetailsRepository;
import com.crimsonlogic.eventmanagement.util.IDGenerator;
import com.crimsonlogic.eventmanagement.exception.BookingNotFoundException;
import com.crimsonlogic.eventmanagement.exception.EventNotFoundException;
import com.crimsonlogic.eventmanagement.exception.InsufficientCapacityException;
import com.crimsonlogic.eventmanagement.exception.InsufficientWalletBalanceException;
import com.crimsonlogic.eventmanagement.exception.InvalidBookingStatusException;
import com.crimsonlogic.eventmanagement.exception.TicketPriceNotFoundException;
import com.crimsonlogic.eventmanagement.exception.UserNotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingPaymentsRepository bookingPaymentsRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private TicketPriceRepository ticketPriceRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Books tickets for a specific event for a user.
     *
     * @param userId        The ID of the user booking the tickets.
     * @param eventId       The ID of the event for which tickets are being booked.
     * @param ticketPriceId The ID of the ticket price category.
     * @param numberOfTickets The number of tickets to be booked.
     * @return BookingDto containing booking details.
     */
    @Override
    public BookingDto bookTickets(String userId, String eventId, String ticketPriceId, int numberOfTickets) {
        // Validate required fields
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID is required.");
        }
        if (eventId == null || eventId.isEmpty()) {
            throw new IllegalArgumentException("Event ID is required.");
        }
        if (numberOfTickets <= 0) {
            throw new IllegalArgumentException("Number of tickets must be greater than zero.");
        }

        // Fetch user details
        UserDetails user = userDetailsRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        log.info("User Full Name: {}", user.getFullName());

        // Fetch event details
        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));

        // Check venue capacity
        Venue venue = venueRepository.findByVenueForEvent(event);
        if (venue == null || venue.getCapacity() < numberOfTickets) {
            throw new InsufficientCapacityException("Not enough capacity available for the venue.");
        }

        log.info("Venue location: {}", venue.getVenueLocation());

        // Create Booking
        Bookings booking = new Bookings();
        booking.setBookingId(IDGenerator.generateBookingID());
        booking.setBookingDate(new Timestamp(System.currentTimeMillis())); // Set current timestamp
        booking.setBookingStatus("CONFIRMED"); // Initial status
        booking.setNoOfTickets(numberOfTickets); // Set the number of tickets booked
        booking.setBookingForEvent(event); // Link to the event
        booking.setBookingMadeBy(user); // Link to the user
        bookingRepository.save(booking); // Save the booking

        // Fetch ticket price based on the selected category
        double ticketPrice = getTicketPrice(eventId, ticketPriceId);
        double totalPrice = numberOfTickets * ticketPrice; // Calculate total price

        // Create Booking Payment
        BookingPayments bookingPayment = new BookingPayments();
        bookingPayment.setBookingPaymentId(IDGenerator.generateBookingPaymentID());
        bookingPayment.setPaymentAmount(totalPrice); // Set the payment amount
        bookingPayment.setPaymentStatus("SUCCESS"); // Payment status
        bookingPayment.setPaymentForBooking(booking); // Link payment to the booking
        bookingPaymentsRepository.save(bookingPayment); // Save the payment

        // Reduce Venue Capacity
        venue.setCapacity(venue.getCapacity() - numberOfTickets);
        venueRepository.save(venue); // Update the venue's capacity

        // Deduct from User Wallet
        Wallet wallet = walletRepository.findByWalletForUser(user);
        if (wallet.getAmount() < totalPrice) {
            throw new InsufficientWalletBalanceException("Insufficient wallet balance for user: " + userId);
        }
        wallet.setAmount(wallet.getAmount() - totalPrice); // Deduct the total price from wallet
        walletRepository.save(wallet); // Save updated wallet

        log.info("Tickets booked successfully for user: {} for event: {}. Booking ID: {}", userId, eventId,
                booking.getBookingId());

        // Map to BookingDto and return it
        BookingDto responseDto = modelMapper.map(booking, BookingDto.class);
        responseDto.setTotalPrice(totalPrice); // Set total price in response DTO
        responseDto.setBookingId(booking.getBookingId()); // Set booking ID in response DTO
        responseDto.setFullName(user.getFullName()); // Set user's full name
        responseDto.setLocation(venue.getVenueLocation()); // Set venue location

        log.info("BookingDto: {}", responseDto); // Log the response DTO

        return responseDto; // Return the response DTO
    }

    /**
     * Retrieves the price for a specific ticket category.
     *
     * @param eventId       The ID of the event.
     * @param ticketPriceId The ID of the ticket price category.
     * @return The price of the ticket.
     */
    private double getTicketPrice(String eventId, String ticketPriceId) {
        TicketPrice ticketPrice = ticketPriceRepository.findById(ticketPriceId)
                .orElseThrow(() -> new TicketPriceNotFoundException("Ticket price not found for the selected category."));
        return ticketPrice.getPrice(); // Return the ticket price
    }

    /**
     * Retrieves all bookings made by a specific user.
     *
     * @param userId The ID of the user whose bookings are to be retrieved.
     * @return A list of BookingDto containing booking details.
     */
    @Override
    public List<BookingDto> getUserBookings(String userId) {
        // Validate required fields
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        // Fetch user details
        UserDetails user = userDetailsRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        log.info("Fetching bookings for user: {}", user.getFullName());

        // Fetch bookings made by the user
        List<Bookings> userBookings = bookingRepository.findByBookingMadeBy_UserDetailsId(userId);

        if (userBookings.isEmpty()) {
            log.warn("No bookings found for user: {}", user.getFullName());
            return Collections.emptyList(); // Return empty list if no bookings
        }

        // Create a list to hold BookingDto responses
        List<BookingDto> bookingDtos = new ArrayList<>();

        // Iterate through bookings and map them to BookingDto
        for (Bookings booking : userBookings) {
            BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);

            // Set the full name
            bookingDto.setFullName(user.getFullName());

            // Fetch event name using eventId from the booking
            Events event = booking.getBookingForEvent(); // Assuming this gets the related event
            if (event != null) {
                bookingDto.setEventName(event.getEventName()); // Set event name in the DTO
                bookingDto.setEventId(event.getEventId()); // Set eventId if needed
            }

            // Fetch the amount paid from BookingPayments
            BookingPayments payment = bookingPaymentsRepository.findByPaymentForBooking(booking);
            if (payment != null) {
                bookingDto.setTotalPrice(payment.getPaymentAmount()); // Set total amount paid
            }

            bookingDtos.add(bookingDto); // Add the booking DTO to the list
        }

        log.info("Bookings fetched successfully for user: {}", user.getFullName());

        return bookingDtos; // Return the list of booking DTOs
    }

    /**
     * Cancels a booking by its ID.
     *
     * @param bookingId The ID of the booking to be canceled.
     */
    @Override
    public void cancelBooking(String bookingId) {
        // Fetch the booking by ID
        Bookings booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + bookingId));

        // Check if the booking is confirmed
        if (!"CONFIRMED".equals(booking.getBookingStatus())) {
            throw new InvalidBookingStatusException("Only confirmed bookings can be cancelled.");
        }

        // Change booking status to "CANCELLED"
        booking.setBookingStatus("CANCELLED");
        bookingRepository.save(booking); // Save the updated booking

        // Increase venue capacity
        Venue venue = venueRepository.findByVenueForEvent(booking.getBookingForEvent());
        venue.setCapacity(venue.getCapacity() + booking.getNoOfTickets()); // Add the cancelled tickets back to capacity
        venueRepository.save(venue); // Save updated venue capacity

        // Refund 50% of the payment to the user's wallet
        BookingPayments payment = bookingPaymentsRepository.findByPaymentForBooking(booking);
        Wallet wallet = walletRepository.findByWalletForUser(booking.getBookingMadeBy());
        double refundAmount = payment.getPaymentAmount() * 0.5; // Calculate refund amount
        wallet.setAmount(wallet.getAmount() + refundAmount); // Add refund to user's wallet
        walletRepository.save(wallet); // Save updated wallet
    }

    /**
     * Retrieves total revenue and total tickets sold for a specific event.
     *
     * @param eventId The ID of the event.
     * @return A map containing total revenue and total tickets sold.
     */
    @Override
    public Map<String, Object> getTotalRevenueAndTicketsForEvent(String eventId) {
        // Validate required fields
        if (eventId == null || eventId.isEmpty()) {
            throw new IllegalArgumentException("Event ID is required.");
        }

        // Fetch total payments for the event
        Double totalRevenue = bookingPaymentsRepository.sumPaymentsByEventId(eventId);

        // Fetch the total number of tickets sold for the event
        Long totalTicketsSold = bookingRepository.sumTicketsByEventId(eventId);

        // Create a response map
        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0); // Default to 0.0 if null
        response.put("totalTicketsSold", totalTicketsSold != null ? totalTicketsSold : 0L); // Default to 0L if null

        return response; // Return the response map
    }
}
