package com.crimsonlogic.eventmanagement.entity;

import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "eventCategories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCategories {
    
    @Id
    @Column(name = "category_id", length = 10)
    private String categoryId;
    
    @Column(name = "category_name", length = 50)
    private String categoryName;
    
    @Column(name = "image_path", length = 255)
    private String imagePath;
    
    @Column(name = "created_at")
    private Timestamp createdAt;
    
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    
    @Column(name = "is_enabled")
    private boolean isEnabled;

}
