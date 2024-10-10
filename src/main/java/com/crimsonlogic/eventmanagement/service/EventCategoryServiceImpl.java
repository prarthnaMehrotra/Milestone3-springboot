package com.crimsonlogic.eventmanagement.service;

import com.crimsonlogic.eventmanagement.payload.EventCategoryDto;
import com.crimsonlogic.eventmanagement.entity.EventCategories;
import com.crimsonlogic.eventmanagement.repository.EventCategoryRepository;
import com.crimsonlogic.eventmanagement.service.EventCategoryService;
import com.crimsonlogic.eventmanagement.util.IDGenerator;
import com.crimsonlogic.eventmanagement.exception.CategoryNotFoundException;
import com.crimsonlogic.eventmanagement.exception.ImageStorageException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventCategoryServiceImpl implements EventCategoryService {

    @Autowired
    private EventCategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Path for storing images, loaded from application properties
    @Value("${image.storage.path}")
    private String imageStoragePath;

    // Constructor for dependency injection
    public EventCategoryServiceImpl(EventCategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieves all event categories.
     *
     * @return A list of EventCategoryDto representing all categories.
     */
    @Override
    public List<EventCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> modelMapper.map(category, EventCategoryDto.class)) // Convert to DTO
                .collect(Collectors.toList());
    }

    /**
     * Creates a new event category.
     *
     * @param categoryDto The category data transfer object containing category information.
     * @param imageFile The image file associated with the category.
     * @return The created EventCategoryDto.
     */
    @Override
    public EventCategoryDto createCategory(EventCategoryDto categoryDto, MultipartFile imageFile) {
        // Map DTO to entity
        EventCategories category = modelMapper.map(categoryDto, EventCategories.class);

        // Set ID, creation timestamp, enabled status, and updated timestamp
        category.setCategoryId(IDGenerator.generateCategoryID());
        category.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        category.setEnabled(true);
        category.setUpdatedAt(null);

        // Save the image file and store the relative path
        String imagePath = saveImageFile(imageFile);
        category.setImagePath(imagePath); // Store relative path to the image

        // Save the category entity to the repository
        category = categoryRepository.save(category);
        return modelMapper.map(category, EventCategoryDto.class); // Convert back to DTO for return
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
            String folderPath = "D:/Training 2024/reactexamples/event-management/public/images"; // Adjust as needed
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs(); // Create the directory if it doesn't exist
            }

            // Create a unique filename to prevent collisions
            String fileName = "image_" + imageFile.getOriginalFilename();
            File destinationFile = new File(folder, fileName);
            imageFile.transferTo(destinationFile); // Save the file to the destination

            // Return the public URL for accessing the image
            return "/images/" + fileName; // Accessible from the React app
        } catch (IOException e) {
            throw new ImageStorageException("Failed to store image file: " + e.getMessage());
        }
    }

    /**
     * Updates an existing event category.
     *
     * @param id The ID of the category to be updated.
     * @param categoryDto The new category data.
     * @param imageFile The new image file, if any.
     * @return The updated EventCategoryDto.
     * @throws CategoryNotFoundException if the category is not found.
     */
    @Override
    public EventCategoryDto updateCategory(String id, EventCategoryDto categoryDto, MultipartFile imageFile) {
        EventCategories category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + id));

        // Update the category name and set the updated timestamp
        category.setCategoryName(categoryDto.getCategoryName());
        category.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // Update timestamp

        // If a new image file is provided, save it
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImageFile(imageFile);
            category.setImagePath(imagePath);
        }

        // Save the updated category entity
        category = categoryRepository.save(category);
        return modelMapper.map(category, EventCategoryDto.class); // Convert to DTO for return
    }

    /**
     * Deletes an event category.
     *
     * @param id The ID of the category to be deleted.
     * @throws CategoryNotFoundException if the category is not found.
     */
    @Override
    public void deleteCategory(String id) {
        // Check if the category exists before attempting to delete
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Cannot delete. Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id); // Delete the category
    }
}
