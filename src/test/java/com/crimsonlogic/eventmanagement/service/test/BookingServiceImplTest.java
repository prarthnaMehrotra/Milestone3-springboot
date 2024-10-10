package com.crimsonlogic.eventmanagement.service.test;

import com.crimsonlogic.eventmanagement.entity.*;
import com.crimsonlogic.eventmanagement.payload.BookingDto;
import com.crimsonlogic.eventmanagement.repository.*;
import com.crimsonlogic.eventmanagement.service.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

	@InjectMocks
	private BookingServiceImpl bookingService;

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private BookingPaymentsRepository bookingPaymentsRepository;

	@Mock
	private EventRepository eventRepository;

	@Mock
	private VenueRepository venueRepository;

	@Mock
	private TicketPriceRepository ticketPriceRepository;

	@Mock
	private WalletRepository walletRepository;

	@Mock
	private UserDetailsRepository userDetailsRepository;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private UserDetails userDetails;

	@Mock
	private Events event;

	@Mock
	private Venue venue;

	@Mock
	private Wallet wallet;

	@Mock
	private Bookings booking;

	@Mock
	private BookingPayments bookingPayment;

	@BeforeEach
	void setUp() {

	}

	@Test
	void bookTickets_Success() {
		String userId = "user1";
		String eventId = "event1";
		String ticketPriceId = "ticket1";
		int numberOfTickets = 2;

		when(userDetailsRepository.findById(userId)).thenReturn(Optional.of(userDetails));

		when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

		when(venueRepository.findByVenueForEvent(event)).thenReturn(venue);
		when(venue.getCapacity()).thenReturn(10);

		TicketPrice ticketPrice = new TicketPrice(ticketPriceId, userId, 50.0, event);
		when(ticketPriceRepository.findById(ticketPriceId)).thenReturn(Optional.of(ticketPrice));

		when(walletRepository.findByWalletForUser(userDetails)).thenReturn(wallet);
		when(wallet.getAmount()).thenReturn(100.0);

		when(bookingRepository.save(any(Bookings.class))).thenReturn(booking);

		when(bookingPaymentsRepository.save(any(BookingPayments.class))).thenReturn(bookingPayment);

		BookingDto bookingDto = new BookingDto();
		bookingDto.setBookingId("BKI-C1BC0B");
		when(modelMapper.map(any(Bookings.class), eq(BookingDto.class))).thenReturn(bookingDto);

		BookingDto result = bookingService.bookTickets(userId, eventId, ticketPriceId, numberOfTickets);

		assertNotNull(result);
		verify(bookingRepository, times(1)).save(any(Bookings.class));
		verify(bookingPaymentsRepository, times(1)).save(any(BookingPayments.class));
	}

	@Test
	void bookTickets_UserNotFound() {
		String userId = "user1";
		String eventId = "event1";
		String ticketPriceId = "ticket1";
		int numberOfTickets = 2;

		when(userDetailsRepository.findById(userId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(RuntimeException.class,
				() -> bookingService.bookTickets(userId, eventId, ticketPriceId, numberOfTickets));

		assertEquals("User not found", exception.getMessage());
	}

	@Test
	void getUserBookings_Success() {
		String userId = "user1";
		List<Bookings> bookingsList = Collections.singletonList(booking);

		when(userDetailsRepository.findById(userId)).thenReturn(Optional.of(userDetails));
		when(bookingRepository.findByBookingMadeBy_UserDetailsId(userId)).thenReturn(bookingsList);
		when(modelMapper.map(booking, BookingDto.class)).thenReturn(new BookingDto());

		List<BookingDto> result = bookingService.getUserBookings(userId);

		assertNotNull(result);
		assertEquals(1, result.size());
		verify(bookingRepository, times(1)).findByBookingMadeBy_UserDetailsId(userId);
	}

	@Test
	void getUserBookings_UserNotFound() {
		String userId = "user1";

		when(userDetailsRepository.findById(userId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(RuntimeException.class, () -> bookingService.getUserBookings(userId));

		assertEquals("User not found", exception.getMessage());
	}

	@Test
	void cancelBooking_Success() {
	    String bookingId = "booking1";

	    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
	    when(booking.getBookingStatus()).thenReturn("CONFIRMED");
	    when(booking.getBookingForEvent()).thenReturn(mock(Events.class));
	    when(booking.getNoOfTickets()).thenReturn(50);

	    when(venueRepository.findByVenueForEvent(any())).thenReturn(venue);
	    when(venue.getCapacity()).thenReturn(100);

	    when(bookingPaymentsRepository.findByPaymentForBooking(booking)).thenReturn(bookingPayment);
	    when(bookingPayment.getPaymentAmount()).thenReturn(100.0);
	    when(walletRepository.findByWalletForUser(any())).thenReturn(wallet);
	    when(wallet.getAmount()).thenReturn(150.0);

	    bookingService.cancelBooking(bookingId);

	    double expectedWalletAmount = 100.0 + (100.0 * 0.5);

	    // Verify interactions
	    verify(bookingRepository, times(1)).save(booking);
	    verify(venueRepository, times(1)).save(venue);
	    verify(walletRepository, times(1)).save(wallet);

	    assertEquals(expectedWalletAmount, wallet.getAmount());
	    verify(venue, times(1)).setCapacity(150);
	}


	@Test
	void getTotalRevenueAndTicketsForEvent_Success() {
		String eventId = "event1";

		when(bookingPaymentsRepository.sumPaymentsByEventId(eventId)).thenReturn(200.0);
		when(bookingRepository.sumTicketsByEventId(eventId)).thenReturn(10L);

		Map<String, Object> result = bookingService.getTotalRevenueAndTicketsForEvent(eventId);

		assertNotNull(result);
		assertEquals(200.0, result.get("totalRevenue"));
		assertEquals(10L, result.get("totalTicketsSold"));
	}

	@Test
	void getTotalRevenueAndTicketsForEvent_EventNotFound() {
		String eventId = "event1";

		when(bookingPaymentsRepository.sumPaymentsByEventId(eventId)).thenReturn(null);
		when(bookingRepository.sumTicketsByEventId(eventId)).thenReturn(null);

		Map<String, Object> result = bookingService.getTotalRevenueAndTicketsForEvent(eventId);

		assertNotNull(result);
		assertEquals(0.0, result.get("totalRevenue"));
		assertEquals(0L, result.get("totalTicketsSold"));
	}

}
