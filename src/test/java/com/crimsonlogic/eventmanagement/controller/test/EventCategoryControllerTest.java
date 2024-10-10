package com.crimsonlogic.eventmanagement.controller.test;

import com.crimsonlogic.eventmanagement.controller.EventCategoryController;
import com.crimsonlogic.eventmanagement.payload.EventCategoryDto;
import com.crimsonlogic.eventmanagement.service.EventCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventCategoryControllerTest {

    private EventCategoryService categoryService;
    private EventCategoryController categoryController;

    @BeforeEach
    void setUp() {
        categoryService = Mockito.mock(EventCategoryService.class);
        categoryController = new EventCategoryController(categoryService);
    }

    @Test
    void testGetAllCategories() {
        List<EventCategoryDto> categories = Collections.singletonList(new EventCategoryDto());
        when(categoryService.getAllCategories()).thenReturn(categories);

        List<EventCategoryDto> response = categoryController.getAllCategories();

        assertEquals(categories, response);
        verify(categoryService).getAllCategories();
    }

    @Test
    void testCreateCategory() {
        EventCategoryDto categoryDto = new EventCategoryDto();
        MultipartFile imageFile = mock(MultipartFile.class); // Mocking the MultipartFile
        when(categoryService.createCategory(categoryDto, imageFile)).thenReturn(categoryDto);

        ResponseEntity<EventCategoryDto> response = categoryController.createCategory(categoryDto, imageFile);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(categoryDto, response.getBody());
        verify(categoryService).createCategory(categoryDto, imageFile);
    }

    @Test
    void testUpdateCategory() {
        String categoryId = "category-1";
        EventCategoryDto categoryDto = new EventCategoryDto();
        MultipartFile imageFile = mock(MultipartFile.class); // Mocking the MultipartFile
        when(categoryService.updateCategory(categoryId, categoryDto, imageFile)).thenReturn(categoryDto);

        ResponseEntity<EventCategoryDto> response = categoryController.updateCategory(categoryId, categoryDto, imageFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryDto, response.getBody());
        verify(categoryService).updateCategory(categoryId, categoryDto, imageFile);
    }

    @Test
    void testDeleteCategory() {
        String categoryId = "category-1";

        categoryController.deleteCategory(categoryId);

        verify(categoryService).deleteCategory(categoryId);
    }
}
