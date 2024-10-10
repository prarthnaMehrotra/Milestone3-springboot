package com.crimsonlogic.eventmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookingPayments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingPayments {
    
    @Id
    @Column(name = "booking_payment_id", length = 10)
    private String bookingPaymentId;
    
    @Column(name = "payment_amount")
    private double paymentAmount;
    
    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    @OneToOne
    @JoinColumn(name = "payment_for_booking")
    private Bookings paymentForBooking;
}
