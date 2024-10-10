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
@Table(name = "ticketPrice")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketPrice {
    
    @Id
    @Column(name = "ticket_price_id", length = 10)
    private String ticketPriceId;
    
    @Column(name = "price_category", length = 50)
    private String priceCategory;
    
    @Column(name = "price")
    private double price;

    @ManyToOne
    @JoinColumn(name = "ticket_for_event")
    private Events ticketForEvent;
}
