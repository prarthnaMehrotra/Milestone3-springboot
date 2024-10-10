package com.crimsonlogic.eventmanagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {
	
    private String userDetailsId;
    private String fullName;
    private String contactNumber;
    private String alternateNumber;
    private String dateOfBirth;
    private Boolean isApproved;
    private String email;
    private double walletAmount;
    
}
