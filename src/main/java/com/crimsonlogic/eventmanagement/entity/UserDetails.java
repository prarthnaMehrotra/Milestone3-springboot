package com.crimsonlogic.eventmanagement.entity;

import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userDetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {
	
    @Id
    @Column(name = "user_details_id", length = 10)
    private String userDetailsId;
    
    @Column(name = "full_name", length = 100)
    private String fullName;
    
    @Column(name = "contact_number", length = 10)
    private String contactNumber;
    
    @Column(name = "alternate_number", length = 10)
    private String alternateNumber;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "is_approved")
    private Boolean isApproved;
    
    @Column(name = "created_at")
    private Timestamp createdAt;

    @OneToOne
    @JoinColumn(name = "details_of_user")
    private UserAuthentication detailsOfUser;
}
