package com.crimsonlogic.eventmanagement.controller;

import com.crimsonlogic.eventmanagement.payload.EventCategoryDto;
import com.crimsonlogic.eventmanagement.service.EventCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3001")
public class EventCategoryController {

    @Autowired
    private EventCategoryService categoryService; // Service for handling category logic

    /**
     * Constructor for dependency injection.
     *
     * @param categoryService The event category service to be injected.
     */
    public EventCategoryController(EventCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves all event categories.
     *
     * @return A list of EventCategoryDto objects representing all categories.
     */
    @GetMapping
    public List<EventCategoryDto> getAllCategories() {
        return categoryService.getAllCategories(); // Fetch and return all categories
    }

    /**
     * Creates a new event category.
     *
     * @param categoryDto The data transfer object containing category details.
     * @param imageFile   The image file associated with the category.
     * @return A ResponseEntity containing the created EventCategoryDto and the HTTP status.
     */
    @PostMapping
    public ResponseEntity<EventCategoryDto> createCategory(@ModelAttribute EventCategoryDto categoryDto,
                                                           @RequestParam("image") MultipartFile imageFile) {
        EventCategoryDto createdCategory = categoryService.createCategory(categoryDto, imageFile); // Create a new category
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory); // Return the created category with a 201 status
    }

    /**
     * Updates an existing event category by its ID.
     *
     * @param id          The ID of the category to update.
     * @param categoryDto The updated category details.
     * @param imageFile   The new image file for the category (optional).
     * @return A ResponseEntity containing the updated EventCategoryDto and the HTTP status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventCategoryDto> updateCategory(@PathVariable String id,
                                                           @ModelAttribute EventCategoryDto categoryDto,
                                                           @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        EventCategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto, imageFile); // Update the category
        return ResponseEntity.ok(updatedCategory); // Return the updated category with a 200 status
    }

    /**
     * Deletes an event category by its ID.
     *
     * @param id The ID of the category to delete.
     */
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id); // Call the service to delete the category
    }
}
