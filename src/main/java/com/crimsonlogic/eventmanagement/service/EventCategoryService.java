package com.crimsonlogic.eventmanagement.service;

import com.crimsonlogic.eventmanagement.payload.EventCategoryDto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface EventCategoryService {

	EventCategoryDto createCategory(EventCategoryDto categoryDto, MultipartFile imageFile);

	EventCategoryDto updateCategory(String id, EventCategoryDto categoryDto, MultipartFile imageFile);

	List<EventCategoryDto> getAllCategories();

	void deleteCategory(String id);

}
