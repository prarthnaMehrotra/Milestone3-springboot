package com.crimsonlogic.eventmanagement.controller.test;

import com.crimsonlogic.eventmanagement.controller.EventController;
import com.crimsonlogic.eventmanagement.payload.EventDto;
import com.crimsonlogic.eventmanagement.payload.SponsorDto;
import com.crimsonlogic.eventmanagement.payload.TicketPriceDto;
import com.crimsonlogic.eventmanagement.payload.VenueDto;
import com.crimsonlogic.eventmanagement.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventControllerTest {

    private EventService eventService;
    private EventController eventController;

    @BeforeEach
    void setUp() {
        eventService = Mockito.mock(EventService.class);
        eventController = new EventController(eventService);
    }

    @Test
    void testGetAllEvents() {
        List<EventDto> events = Collections.singletonList(new EventDto());
        when(eventService.getAllEvents()).thenReturn(events);

        List<EventDto> response = eventController.getAllEvents();

        assertEquals(events, response);
        verify(eventService).getAllEvents();
    }

    @Test
    void testGetAllEventsByDate() {
        List<EventDto> events = Collections.singletonList(new EventDto());
        when(eventService.getAllEventsByDate()).thenReturn(events);

        List<EventDto> response = eventController.getAllEventsByDate();

        assertEquals(events, response);
        verify(eventService).getAllEventsByDate();
    }

    @Test
    void testGetEventsByOrganizer() {
        String userDetailsId = "user-1";
        List<EventDto> events = Collections.singletonList(new EventDto());
        when(eventService.getEventsByOrganizer(userDetailsId)).thenReturn(events);

        ResponseEntity<List<EventDto>> response = eventController.getEventsByOrganizer(userDetailsId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(events, response.getBody());
        verify(eventService).getEventsByOrganizer(userDetailsId);
    }

    @Test
    void testCreateEvent() {
        String userDetailsId = "user-1";
        EventDto eventDto = new EventDto();
        eventDto.setEventName("Test Event");
        eventDto.setDescription("Description of Test Event");
        eventDto.setDate(LocalDate.now());
        eventDto.setTime(LocalTime.now());
        eventDto.setCategoryId("category-1");
        MultipartFile imageFile = mock(MultipartFile.class);

        when(eventService.createEvent(eq(userDetailsId), any(EventDto.class), any(MultipartFile.class)))
            .thenReturn(eventDto);

        ResponseEntity<EventDto> response = eventController.createEvent(
                userDetailsId,
                eventDto.getEventName(),
                eventDto.getDescription(),
                eventDto.getDate(),
                eventDto.getTime(),
                eventDto.getCategoryId(),
                imageFile
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(eventDto, response.getBody());
        verify(eventService).createEvent(eq(userDetailsId), any(EventDto.class), any(MultipartFile.class));
    }

    @Test
    void testAddSponsors() {
        String eventId = "event-1";
        SponsorDto sponsorDto = new SponsorDto();

        ResponseEntity<Void> response = eventController.addSponsors(eventId, sponsorDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(eventService).addSponsors(eventId, sponsorDto);
    }

    @Test
    void testAddTicketPrices() {
        String eventId = "event-1";
        TicketPriceDto ticketPriceDto = new TicketPriceDto();

        ResponseEntity<Void> response = eventController.addTicketPrices(eventId, ticketPriceDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(eventService).addTicketPrices(eventId, ticketPriceDto);
    }

    @Test
    void testAddVenue() {
        String eventId = "event-1";
        VenueDto venueDto = new VenueDto();

        ResponseEntity<Void> response = eventController.addVenue(eventId, venueDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(eventService).addVenue(eventId, venueDto);
    }

    @Test
    void testGetTicketPrices() {
        String eventId = "event-1";
        List<TicketPriceDto> ticketPrices = Collections.singletonList(new TicketPriceDto());
        when(eventService.getTicketPricesForEvent(eventId)).thenReturn(ticketPrices);

        ResponseEntity<List<TicketPriceDto>> response = eventController.getTicketPrices(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ticketPrices, response.getBody());
        verify(eventService).getTicketPricesForEvent(eventId);
    }

    @Test
    void testGetEventDetails() {
        String eventId = "event-1";
        EventDto eventDetails = new EventDto();
        when(eventService.getEventDetails(eventId)).thenReturn(eventDetails);

        ResponseEntity<EventDto> response = eventController.getEventDetails(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventDetails, response.getBody());
        verify(eventService).getEventDetails(eventId);
    }
}
