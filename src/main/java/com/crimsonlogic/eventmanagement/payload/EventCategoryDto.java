package com.crimsonlogic.eventmanagement.payload;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCategoryDto {
	
	private String categoryId;
	private String categoryName;
	private String imagePath;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private boolean isEnabled;
	
}
