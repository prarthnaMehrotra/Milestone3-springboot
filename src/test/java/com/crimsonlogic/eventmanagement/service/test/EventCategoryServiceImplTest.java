package com.crimsonlogic.eventmanagement.service.test;

import com.crimsonlogic.eventmanagement.entity.EventCategories;
import com.crimsonlogic.eventmanagement.payload.EventCategoryDto;
import com.crimsonlogic.eventmanagement.repository.EventCategoryRepository;
import com.crimsonlogic.eventmanagement.service.EventCategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventCategoryServiceImplTest {

    @InjectMocks
    private EventCategoryServiceImpl eventCategoryService;

    @Mock
    private EventCategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MultipartFile imageFile;

    @BeforeEach
    void setUp() {
       
    }

    @Test
    void getAllCategories() {
        List<EventCategories> categories = new ArrayList<>();
        EventCategories category = new EventCategories();
        category.setCategoryId("1");
        category.setCategoryName("Test Category");
        categories.add(category);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(modelMapper.map(any(EventCategories.class), eq(EventCategoryDto.class)))
                .thenReturn(new EventCategoryDto());

        List<EventCategoryDto> result = eventCategoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void createCategory() throws IOException {
        EventCategoryDto categoryDto = new EventCategoryDto();
        categoryDto.setCategoryName("New Category");

        EventCategories category = new EventCategories();
        category.setCategoryId("1");
        category.setCategoryName("New Category");
        category.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        category.setEnabled(true);

        when(modelMapper.map(categoryDto, EventCategories.class)).thenReturn(category);
        when(categoryRepository.save(any(EventCategories.class))).thenReturn(category);
        when(imageFile.getOriginalFilename()).thenReturn("test.jpg");

        // Stub the void method correctly
        doNothing().when(imageFile).transferTo(any(File.class));

        // Here, we call the actual method rather than stubbing it
        String imagePath = eventCategoryService.saveImageFile(imageFile);
        when(eventCategoryService.saveImageFile(imageFile)).thenReturn(imagePath);

        EventCategoryDto result = eventCategoryService.createCategory(categoryDto, imageFile);

        assertNotNull(result);
        assertEquals("New Category", result.getCategoryName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void updateCategory() throws IOException {
        String categoryId = "1";
        EventCategoryDto categoryDto = new EventCategoryDto();
        categoryDto.setCategoryName("Updated Category");

        EventCategories existingCategory = new EventCategories();
        existingCategory.setCategoryId(categoryId);
        existingCategory.setCategoryName("Old Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(modelMapper.map(existingCategory, EventCategoryDto.class)).thenReturn(new EventCategoryDto());

        when(imageFile.getOriginalFilename()).thenReturn("updated.jpg");
        doNothing().when(imageFile).transferTo(any(File.class));

        // Call the actual method for image saving
        String imagePath = eventCategoryService.saveImageFile(imageFile);
        when(eventCategoryService.saveImageFile(imageFile)).thenReturn(imagePath);

        EventCategoryDto result = eventCategoryService.updateCategory(categoryId, categoryDto, imageFile);

        assertNotNull(result);
        assertEquals("Updated Category", existingCategory.getCategoryName());
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    void deleteCategory() {
        String categoryId = "1";

        doNothing().when(categoryRepository).deleteById(categoryId);
        eventCategoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }
}
