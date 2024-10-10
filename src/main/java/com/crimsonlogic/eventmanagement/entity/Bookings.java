package com.crimsonlogic.eventmanagement.entity;

import java.sql.Timestamp;
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
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bookings {
    
    @Id
    @Column(name = "booking_id", length = 10)
    private String bookingId;
    
    @Column(name = "booking_date")
    private Timestamp bookingDate;
    
    @Column(name = "booking_status", length = 20)
    private String bookingStatus;
    
    @Column(name = "no_of_tickets")
    private int noOfTickets;

    @ManyToOne
    @JoinColumn(name = "booking_for_event")
    private Events bookingForEvent;

    @ManyToOne
    @JoinColumn(name = "booking_made_by")
    private UserDetails bookingMadeBy;
}
