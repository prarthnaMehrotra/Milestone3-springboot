package com.crimsonlogic.eventmanagement.controller;

import com.crimsonlogic.eventmanagement.payload.EventDto;
import com.crimsonlogic.eventmanagement.payload.SponsorDto;
import com.crimsonlogic.eventmanagement.payload.TicketPriceDto;
import com.crimsonlogic.eventmanagement.payload.VenueDto;
import com.crimsonlogic.eventmanagement.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3001")
@Slf4j
public class EventController {

    @Autowired
    private EventService eventService; // Service for handling event logic

    /**
     * Constructor for dependency injection.
     *
     * @param eventService The event service to be injected.
     */
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    
    /**
     * Retrieves all events.
     *
     * @return A list of EventDto objects representing all events.
     */
    @GetMapping("/getallevents")
    public List<EventDto> getAllEvents() {
        return eventService.getAllEvents(); // Fetch and return all events
    }

    /**
     * Retrieves all events sorted by date.
     *
     * @return A list of EventDto objects sorted by date.
     */
    @GetMapping
    public List<EventDto> getAllEventsByDate() {
        return eventService.getAllEventsByDate(); // Fetch events sorted by date
    }

    /**
     * Retrieves events created by a specific organizer.
     *
     * @param userDetailsId The ID of the user who is the organizer.
     * @return A ResponseEntity containing a list of EventDto objects for the organizer.
     */
    @GetMapping("/organizer/{userDetailsId}")
    public ResponseEntity<List<EventDto>> getEventsByOrganizer(@PathVariable String userDetailsId) {
        List<EventDto> events = eventService.getEventsByOrganizer(userDetailsId); // Fetch events by organizer ID
        return ResponseEntity.ok(events); // Return the list of events with a 200 status
    }

    /**
     * Creates a new event.
     *
     * @param userDetailsId The ID of the user creating the event.
     * @param eventName The name of the event.
     * @param description A description of the event.
     * @param date The date of the event.
     * @param time The time of the event.
     * @param categoryId The ID of the category to which the event belongs.
     * @param imageFile The image file associated with the event.
     * @return A ResponseEntity containing the created EventDto and the HTTP status.
     */
    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestParam("userDetailsId") String userDetailsId,
                                                 @RequestParam("eventName") String eventName,
                                                 @RequestParam("description") String description,
                                                 @RequestParam("date") LocalDate date,
                                                 @RequestParam("time") LocalTime time,
                                                 @RequestParam("categoryId") String categoryId,
                                                 @RequestParam("image") MultipartFile imageFile) {
        
        // Create an EventDto from the parameters
        EventDto eventDto = new EventDto();
        eventDto.setEventName(eventName);
        eventDto.setDescription(description);
        eventDto.setDate(date);
        eventDto.setTime(time);
        eventDto.setCategoryId(categoryId);

        // Log the received data for debugging
        log.info("Creating event with details: {}", eventDto);
        log.info("User ID: {}", userDetailsId);

        // Call the service to create the event
        EventDto createdEvent = eventService.createEvent(userDetailsId, eventDto, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent); // Return the created event with a 201 status
    }

    /**
     * Adds sponsors to a specific event.
     *
     * @param eventId The ID of the event to which sponsors are being added.
     * @param sponsors The SponsorDto containing sponsor information.
     * @return A ResponseEntity with a 201 status indicating successful creation.
     */
    @PostMapping("/{eventId}/sponsors")
    public ResponseEntity<Void> addSponsors(@PathVariable("eventId") String eventId, @RequestBody SponsorDto sponsors) {
        eventService.addSponsors(eventId, sponsors); // Call the service to add sponsors
        return ResponseEntity.status(HttpStatus.CREATED).build(); // Return a 201 status
    }

    /**
     * Adds ticket prices for a specific event.
     *
     * @param eventId The ID of the event to which ticket prices are being added.
     * @param ticketPrices The TicketPriceDto containing ticket price information.
     * @return A ResponseEntity with a 201 status indicating successful creation.
     */
    @PostMapping("/{eventId}/ticketPrices")
    public ResponseEntity<Void> addTicketPrices(@PathVariable String eventId,
                                                 @RequestBody TicketPriceDto ticketPrices) {
        eventService.addTicketPrices(eventId, ticketPrices); // Call the service to add ticket prices
        return ResponseEntity.status(HttpStatus.CREATED).build(); // Return a 201 status
    }

    /**
     * Adds venue information for a specific event.
     *
     * @param eventId The ID of the event to which venue information is being added.
     * @param venue The VenueDto containing venue information.
     * @return A ResponseEntity with a 201 status indicating successful creation.
     */
    @PostMapping("/{eventId}/venue")
    public ResponseEntity<Void> addVenue(@PathVariable String eventId, @RequestBody VenueDto venue) {
        eventService.addVenue(eventId, venue); // Call the service to add venue information
        return ResponseEntity.status(HttpStatus.CREATED).build(); // Return a 201 status
    }

    /**
     * Retrieves ticket prices for a specific event.
     *
     * @param eventId The ID of the event for which to retrieve ticket prices.
     * @return A ResponseEntity containing a list of TicketPriceDto objects for the event.
     */
    @GetMapping("/{eventId}/ticketPrices")
    public ResponseEntity<List<TicketPriceDto>> getTicketPrices(@PathVariable String eventId) {
        List<TicketPriceDto> ticketPrices = eventService.getTicketPricesForEvent(eventId); // Fetch ticket prices for the event
        return ResponseEntity.ok(ticketPrices); // Return the list of ticket prices with a 200 status
    }

    /**
     * Retrieves detailed information about a specific event.
     *
     * @param eventId The ID of the event to retrieve details for.
     * @return A ResponseEntity containing the EventDto with event details.
     */
    @GetMapping("/{eventId}/details")
    public ResponseEntity<EventDto> getEventDetails(@PathVariable String eventId) {
        EventDto eventDetails = eventService.getEventDetails(eventId); // Fetch event details
        return ResponseEntity.ok(eventDetails); // Return the event details with a 200 status
    }
}
