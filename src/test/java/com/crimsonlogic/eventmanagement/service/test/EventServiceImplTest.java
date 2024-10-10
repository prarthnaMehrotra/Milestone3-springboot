package com.crimsonlogic.eventmanagement.service.test;

import com.crimsonlogic.eventmanagement.entity.EventCategories;
import com.crimsonlogic.eventmanagement.entity.Events;
import com.crimsonlogic.eventmanagement.entity.Sponsors;
import com.crimsonlogic.eventmanagement.entity.TicketPrice;
import com.crimsonlogic.eventmanagement.entity.UserDetails;
import com.crimsonlogic.eventmanagement.entity.Venue;
import com.crimsonlogic.eventmanagement.payload.EventDto;
import com.crimsonlogic.eventmanagement.payload.SponsorDto;
import com.crimsonlogic.eventmanagement.payload.TicketPriceDto;
import com.crimsonlogic.eventmanagement.payload.VenueDto;
import com.crimsonlogic.eventmanagement.repository.BookingRepository;
import com.crimsonlogic.eventmanagement.repository.EventCategoryRepository;
import com.crimsonlogic.eventmanagement.repository.EventRepository;
import com.crimsonlogic.eventmanagement.repository.SponsorRepository;
import com.crimsonlogic.eventmanagement.repository.TicketPriceRepository;
import com.crimsonlogic.eventmanagement.repository.UserDetailsRepository;
import com.crimsonlogic.eventmanagement.repository.VenueRepository;
import com.crimsonlogic.eventmanagement.service.EventServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

    private EventServiceImpl eventService;

    private EventRepository eventRepository;
    private EventCategoryRepository eventCategoryRepository;
    private UserDetailsRepository userDetailsRepository;
    private SponsorRepository sponsorRepository;
    private TicketPriceRepository ticketPriceRepository;
    private VenueRepository venueRepository;
    private BookingRepository bookingRepository;
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        eventRepository = Mockito.mock(EventRepository.class);
        eventCategoryRepository = Mockito.mock(EventCategoryRepository.class);
        userDetailsRepository = Mockito.mock(UserDetailsRepository.class);
        sponsorRepository = Mockito.mock(SponsorRepository.class);
        ticketPriceRepository = Mockito.mock(TicketPriceRepository.class);
        venueRepository = Mockito.mock(VenueRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        modelMapper = new ModelMapper();

        eventService = new EventServiceImpl(eventRepository, modelMapper);
        eventService.eventCategoriesRepository = eventCategoryRepository;
        eventService.userDetailsRepository = userDetailsRepository;
        eventService.sponsorRepository = sponsorRepository;
        eventService.ticketPriceRepository = ticketPriceRepository;
        eventService.venueRepository = venueRepository;
        eventService.bookingsRepository = bookingRepository;
    }

    @Test
    void testGetAllEvents() {
        // Arrange
        Events event = new Events();
        event.setEventId("event-1");
        event.setEventName("Test Event");
        when(eventRepository.findAll()).thenReturn(List.of(event));

        // Act
        List<EventDto> events = eventService.getAllEvents();

        // Assert
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals("Test Event", events.get(0).getEventName());
        verify(eventRepository).findAll();
    }

    @Test
    void testGetAllEventsByDate() {
        // Arrange
        Events event = new Events();
        event.setEventId("event-1");
        event.setEventName("Future Event");
        event.setDate(LocalDate.now().plusDays(1)); // Set a future date
        when(eventRepository.findAll()).thenReturn(List.of(event));

        // Act
        List<EventDto> events = eventService.getAllEventsByDate();

        // Assert
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals("Future Event", events.get(0).getEventName());
    }

    @Test
    void testGetEventsByOrganizer() {
        // Arrange
        String userDetailsId = "user-1";
        Events event = new Events();
        event.setEventId("event-1");
        event.setEventName("Organizer Event");
        when(eventRepository.findByCreatedBy_UserDetailsId(userDetailsId)).thenReturn(List.of(event));

        // Act
        List<EventDto> events = eventService.getEventsByOrganizer(userDetailsId);

        // Assert
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals("Organizer Event", events.get(0).getEventName());
        verify(eventRepository).findByCreatedBy_UserDetailsId(userDetailsId);
    }

    @Test
    void testCreateEvent() {
        // Arrange
        String userDetailsId = "user-1";
        EventDto eventDto = new EventDto();
        eventDto.setEventName("New Event");
        eventDto.setCategoryId("category-1");
        
        UserDetails user = new UserDetails();
        user.setUserDetailsId(userDetailsId);

        EventCategories category = new EventCategories();
        category.setCategoryId("category-1");

        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.of(user));
        when(eventCategoryRepository.findById("category-1")).thenReturn(Optional.of(category));
        when(eventRepository.save(any(Events.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        // Mock MultipartFile
        MultipartFile imageFile = Mockito.mock(MultipartFile.class);
        when(imageFile.getOriginalFilename()).thenReturn("event-image.jpg");
        
        // Act
        EventDto createdEvent = eventService.createEvent(userDetailsId, eventDto, imageFile);

        // Assert
        assertNotNull(createdEvent);
        assertEquals("New Event", createdEvent.getEventName());
        verify(eventRepository).save(any(Events.class));
    }

    @Test
    void testAddSponsors() {
        // Arrange
        String eventId = "event-1";
        SponsorDto sponsorDto = new SponsorDto();
        sponsorDto.setSponsorName("Test Sponsor");
        sponsorDto.setContactNumber("1234567890");

        Events event = new Events();
        event.setEventId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        eventService.addSponsors(eventId, sponsorDto);

        // Assert
        verify(sponsorRepository).save(any(Sponsors.class));
    }

    @Test
    void testAddTicketPrices() {
        // Arrange
        String eventId = "event-1";
        TicketPriceDto ticketPriceDto = new TicketPriceDto();
        ticketPriceDto.setPrice(100.0);
        ticketPriceDto.setPriceCategory("VIP");

        Events event = new Events();
        event.setEventId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        eventService.addTicketPrices(eventId, ticketPriceDto);

        // Assert
        verify(ticketPriceRepository).save(any(TicketPrice.class));
    }

    @Test
    void testAddVenue() {
        // Arrange
        String eventId = "event-1";
        VenueDto venueDto = new VenueDto();
        venueDto.setVenueLocation("Test Venue");
        venueDto.setMapsLink("http://maps.link");
        venueDto.setCapacity(100);

        Events event = new Events();
        event.setEventId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        eventService.addVenue(eventId, venueDto);

        // Assert
        verify(venueRepository).save(any(Venue.class));
    }

    @Test
    void testGetTicketPricesForEvent() {
        // Arrange
        String eventId = "event-1";
        TicketPrice ticketPrice = new TicketPrice();
        ticketPrice.setPrice(100.0);
        ticketPrice.setPriceCategory("VIP");

        Events event = new Events();
        event.setEventId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(ticketPriceRepository.findByTicketForEvent_EventId(eventId)).thenReturn(List.of(ticketPrice));

        // Act
        List<TicketPriceDto> ticketPrices = eventService.getTicketPricesForEvent(eventId);

        // Assert
        assertNotNull(ticketPrices);
        assertEquals(1, ticketPrices.size());
        assertEquals(100.0, ticketPrices.get(0).getPrice());
    }

    @Test
    void testGetVenueForEvent() {
        // Arrange
        String eventId = "event-1";
        Venue venue = new Venue();
        venue.setVenueLocation("Test Venue");

        Events event = new Events();
        event.setEventId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(venueRepository.findByVenueForEvent(event)).thenReturn(venue);

        // Act
        VenueDto venueDto = eventService.getVenueForEvent(eventId);

        // Assert
        assertNotNull(venueDto);
        assertEquals("Test Venue", venueDto.getVenueLocation());
    }

    @Test
    void testGetSponsorsForEvent() {
        // Arrange
        String eventId = "event-1";
        Sponsors sponsor = new Sponsors();
        sponsor.setSponsorName("Test Sponsor");

        when(sponsorRepository.findBySponsorForEvent_EventId(eventId)).thenReturn(List.of(sponsor));

        // Act
        List<SponsorDto> sponsors = eventService.getSponsorsForEvent(eventId);

        // Assert
        assertNotNull(sponsors);
        assertEquals(1, sponsors.size());
        assertEquals("Test Sponsor", sponsors.get(0).getSponsorName());
    }

    @Test
    void testGetEventDetails() {
        // Arrange
        String eventId = "event-1";
        Events event = new Events();
        event.setEventId(eventId);
        event.setEventName("Test Event");

        Venue venue = new Venue();
        venue.setCapacity(100);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(venueRepository.findByVenueForEvent(event)).thenReturn(venue);
        when(bookingRepository.countByBookingForEvent(event)).thenReturn((int) 10L);

        // Act
        EventDto eventDto = eventService.getEventDetails(eventId);

        // Assert
        assertNotNull(eventDto);
        assertEquals("Test Event", eventDto.getEventName());
        assertEquals(100, eventDto.getTotalCapacity());
        assertEquals(10, eventDto.getBookedTickets());
    }
}
