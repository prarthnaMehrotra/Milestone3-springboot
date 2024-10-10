package com.crimsonlogic.eventmanagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

	private String email;
	private String role;
	private String userId;
	private String userDetailsId;

}
