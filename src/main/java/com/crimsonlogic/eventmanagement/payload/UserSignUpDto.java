package com.crimsonlogic.eventmanagement.payload;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpDto {
	
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
    private String contactNumber;
    private String alternateNumber;
    private LocalDate dateOfBirth;
}
