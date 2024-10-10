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
@Table(name = "organizerPayments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizerPayments {
    
    @Id
    @Column(name = "organizer_payment_id", length = 10)
    private String paymentId;
    
    @Column(name = "total_amount")
    private double totalAmount;
    
    @Column(name = "commission_amount")
    private double commissionAmount;
    
    @Column(name = "payment_date")
    private Timestamp paymentDate;

    @ManyToOne
    @JoinColumn(name = "payment_made_by")
    private UserDetails paymentMadeBy;
}
