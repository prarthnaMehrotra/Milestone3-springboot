package com.crimsonlogic.eventmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sponsors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sponsors {
    
    @Id
    @Column(name = "sponsor_id", length = 10)
    private String sponsorId;
    
    @Column(name = "sponsor_name", length = 50)
    private String sponsorName;
    
    @Column(name = "contact_number", length = 10)
    private String contactNumber;

    @ManyToOne
    @JoinColumn(name = "sponsor_for_event")
    private Events sponsorForEvent;
}
