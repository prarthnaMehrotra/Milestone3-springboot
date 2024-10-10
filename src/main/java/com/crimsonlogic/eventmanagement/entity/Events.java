package com.crimsonlogic.eventmanagement.entity;

import java.time.LocalTime;
import java.sql.Timestamp;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Events {
    
    @Id
    @Column(name = "event_id", length = 10)
    private String eventId;
    
    @Column(name = "event_name", length = 50)
    private String eventName;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "date")
    private LocalDate date;
    
    @Column(name = "time")
    private LocalTime time;
    
    @Column(name = "image_path", length = 255)
    private String imagePath;
    
    @Column(name = "created_at")
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserDetails createdBy;

    @ManyToOne
    @JoinColumn(name = "event_category")
    private EventCategories eventCategory;
}
