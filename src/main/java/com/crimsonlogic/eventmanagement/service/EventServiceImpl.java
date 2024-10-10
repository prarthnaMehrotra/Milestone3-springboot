package com.crimsonlogic.eventmanagement.service;

import com.crimsonlogic.eventmanagement.payload.EventDto;
import com.crimsonlogic.eventmanagement.payload.SponsorDto;
import com.crimsonlogic.eventmanagement.payload.TicketPriceDto;
import com.crimsonlogic.eventmanagement.payload.VenueDto;
import com.crimsonlogic.eventmanagement.entity.EventCategories;
import com.crimsonlogic.eventmanagement.entity.Events;
import com.crimsonlogic.eventmanagement.entity.Sponsors;
import com.crimsonlogic.eventmanagement.entity.TicketPrice;
import com.crimsonlogic.eventmanagement.entity.UserDetails;
import com.crimsonlogic.eventmanagement.entity.Venue;
import com.crimsonlogic.eventmanagement.repository.BookingRepository;
import com.crimsonlogic.eventmanagement.repository.EventCategoryRepository;
import com.crimsonlogic.eventmanagement.repository.EventRepository;
import com.crimsonlogic.eventmanagement.repository.SponsorRepository;
import com.crimsonlogic.eventmanagement.repository.TicketPriceRepository;
import com.crimsonlogic.eventmanagement.repository.UserDetailsRepository;
import com.crimsonlogic.eventmanagement.repository.VenueRepository;
import com.crimsonlogic.eventmanagement.service.EventService;
import com.crimsonlogic.eventmanagement.util.IDGenerator;
import com.crimsonlogic.eventmanagement.exception.EventNotFoundException;
import com.crimsonlogic.eventmanagement.exception.UserNotFoundException;
import com.crimsonlogic.eventmanagement.exception.CategoryNotFoundException;
import com.crimsonlogic.eventmanagement.exception.ImageStorageException;

import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    public EventCategoryRepository eventCategoriesRepository;

    @Autowired
    public UserDetailsRepository userDetailsRepository;

    @Autowired
    public SponsorRepository sponsorRepository;

    @Autowired
    public TicketPriceRepository ticketPriceRepository;

    @Autowired
    public VenueRepository venueRepository;

    @Autowired
    public BookingRepository bookingsRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Path for storing images, loaded from application properties
    @Value("${image.storage.path}")
    private String imageStoragePath;

    // Constructor for dependency injection
    public EventServiceImpl(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieves all events from the repository.
     *
     * @return A list of EventDto representing all events.
     */
    @Override
    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(event -> modelMapper.map(event, EventDto.class)) // Convert entity to DTO
                .collect(Collectors.toList());
    }

    /**
     * Retrieves upcoming events, limited to the next 6 events.
     *
     * @return A list of EventDto representing upcoming events.
     */
    @Override
    public List<EventDto> getAllEventsByDate() {
        LocalDate now = LocalDate.now();
        return eventRepository.findAll().stream()
                .filter(event -> event.getDate().isAfter(now)) // Filter for future events
                .sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate())) // Sort by date
                .limit(6) // Limit to 6 events
                .map(event -> modelMapper.map(event, EventDto.class)) // Convert to DTO
                .collect(Collectors.toList());
    }

    /**
     * Retrieves events created by a specific organizer.
     *
     * @param userDetailsId The ID of the user who is the organizer.
     * @return A list of EventDto representing the organizer's events.
     */
    @Override
    public List<EventDto> getEventsByOrganizer(String userDetailsId) {
        List<Events> events = eventRepository.findByCreatedBy_UserDetailsId(userDetailsId);
        return events.stream()
                .map(event -> modelMapper.map(event, EventDto.class)) // Convert to DTO
                .collect(Collectors.toList());
    }

    /**
     * Creates a new event with the provided details.
     *
     * @param userDetailsId The ID of the user creating the event.
     * @param eventDto The event data transfer object containing event information.
     * @param imageFile The image file associated with the event.
     * @return The created EventDto.
     */
    @Override
    public EventDto createEvent(String userDetailsId, EventDto eventDto, MultipartFile imageFile) {
        // Map DTO to entity
        Events event = modelMapper.map(eventDto, Events.class);

        // Fetch the user who is creating the event
        UserDetails user = userDetailsRepository.findById(userDetailsId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userDetailsId));

        // Set event details
        event.setEventId(IDGenerator.generateEventID()); // Generate a unique event ID
        event.setCreatedBy(user);
        event.setCreatedAt(new Timestamp(System.currentTimeMillis())); // Set creation timestamp

        // Set event category if provided
        if (eventDto.getCategoryId() != null) {
            EventCategories category = eventCategoriesRepository.findById(eventDto.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + eventDto.getCategoryId()));
            event.setEventCategory(category);
        }

        // Save the image file and store the relative path
        String imagePath = saveImageFile(imageFile);
        event.setImagePath(imagePath);

        log.info("Event object before saving: {}", event);
        event = eventRepository.save(event); // Save event to repository
        log.info("Event successfully created: {}", event);

        return modelMapper.map(event, EventDto.class); // Convert back to DTO for return
    }

    /**
     * Saves the uploaded image file to the server.
     *
     * @param imageFile The image file to be saved.
     * @return The public URL of the saved image.
     * @throws ImageStorageException if the image file could not be saved.
     */
    public String saveImageFile(MultipartFile imageFile) {
        try {
            // Define the directory path for storing images
            String folderPath = "D:/Training 2024/reactexamples/event-management/public/images";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs(); // Create directory if it doesn't exist
            }

            // Create a unique filename to prevent collisions
            String fileName = "event_" + imageFile.getOriginalFilename();
            File destinationFile = new File(folder, fileName);
            imageFile.transferTo(destinationFile); // Save the file to the destination

            return "/images/" + fileName; // Return the public URL for accessing the image
        } catch (IOException e) {
            throw new ImageStorageException("Failed to store image file: " + e.getMessage());
        }
    }

    /**
     * Adds sponsors to a specific event.
     *
     * @param eventId The ID of the event to which sponsors are being added.
     * @param list The sponsor data transfer object containing sponsor information.
     */
    @Override
    public void addSponsors(String eventId, SponsorDto list) {
        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));

        if (list == null) {
            log.warn("Received null sponsors for eventId: {}", eventId);
            return; // Exit if no sponsor data is provided
        }

        log.info("Received sponsors for eventId {}: {}", eventId, list);
        Sponsors sponsor = new Sponsors();
        sponsor.setSponsorId(IDGenerator.generateSponsorID()); // Generate a unique sponsor ID
        sponsor.setSponsorName(list.getSponsorName());
        sponsor.setContactNumber(list.getContactNumber());
        sponsor.setSponsorForEvent(event); // Associate the sponsor with the event
        sponsorRepository.save(sponsor); // Save sponsor to repository
    }

    /**
     * Adds ticket prices to a specific event.
     *
     * @param eventId The ID of the event to which ticket prices are being added.
     * @param ticketPrices The ticket price data transfer object containing ticket price information.
     */
    @Override
    public void addTicketPrices(String eventId, TicketPriceDto ticketPrices) {
        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));

        if (ticketPrices == null) {
            log.warn("Received null ticket prices for eventId: {}", eventId);
            return; // Exit if no ticket price data is provided
        }

        log.info("Received ticket prices for eventId {}: {}", eventId, ticketPrices);
        TicketPrice ticketPrice = new TicketPrice();
        ticketPrice.setTicketPriceId(IDGenerator.generateTicketPriceID()); // Generate a unique ticket price ID
        ticketPrice.setPriceCategory(ticketPrices.getPriceCategory());
        ticketPrice.setPrice(ticketPrices.getPrice());
        ticketPrice.setTicketForEvent(event); // Associate the ticket price with the event
        ticketPriceRepository.save(ticketPrice); // Save ticket price to repository
    }

    /**
     * Adds venue information for a specific event.
     *
     * @param eventId The ID of the event to which the venue is being added.
     * @param venueDto The venue data transfer object containing venue information.
     */
    @Override
    public void addVenue(String eventId, VenueDto venueDto) {
        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));

        if (venueDto != null) {
            Venue venue = new Venue();
            venue.setVenueId(IDGenerator.generateVenueID()); // Generate a unique venue ID
            venue.setVenueLocation(venueDto.getVenueLocation());
            venue.setMapsLink(venueDto.getMapsLink());
            venue.setCapacity(venueDto.getCapacity());
            venue.setVenueForEvent(event); // Associate the venue with the event
            venueRepository.save(venue); // Save venue to repository
        }
    }

    /**
     * Retrieves ticket prices for a specific event.
     *
     * @param eventId The ID of the event for which to retrieve ticket prices.
     * @return A list of TicketPriceDto representing the ticket prices for the event.
     */
    @Override
    public List<TicketPriceDto> getTicketPricesForEvent(String eventId) {
        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));

        return ticketPriceRepository.findByTicketForEvent_EventId(eventId).stream()
                .map(ticketPrice -> modelMapper.map(ticketPrice, TicketPriceDto.class)) // Convert to DTO
                .collect(Collectors.toList());
    }

    /**
     * Retrieves venue information for a specific event.
     *
     * @param eventId The ID of the event for which to retrieve venue information.
     * @return The VenueDto representing the venue for the event, or null if not found.
     */
    @Override
    public VenueDto getVenueForEvent(String eventId) {
        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));
        Venue venue = venueRepository.findByVenueForEvent(event);
        return venue != null ? modelMapper.map(venue, VenueDto.class) : null; // Convert to DTO if found
    }

    /**
     * Retrieves sponsors for a specific event.
     *
     * @param eventId The ID of the event for which to retrieve sponsors.
     * @return A list of SponsorDto representing the sponsors for the event.
     */
    @Override
    public List<SponsorDto> getSponsorsForEvent(String eventId) {
        return sponsorRepository.findBySponsorForEvent_EventId(eventId).stream()
                .map(sponsor -> modelMapper.map(sponsor, SponsorDto.class)) // Convert to DTO
                .collect(Collectors.toList());
    }

    /**
     * Retrieves detailed information about a specific event.
     *
     * @param eventId The ID of the event for which to retrieve details.
     * @return The EventDto containing details about the event.
     */
    @Override
    public EventDto getEventDetails(String eventId) {
        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));
        Venue venue = venueRepository.findByVenueForEvent(event);

        EventDto eventDto = modelMapper.map(event, EventDto.class); // Convert to DTO
        if (venue != null) {
            eventDto.setTotalCapacity(venue.getCapacity()); // Set total capacity from venue
            eventDto.setBookedTickets(bookingsRepository.countByBookingForEvent(event)); // Count booked tickets
        }

        return eventDto; // Return detailed event information
    }
}
