package com.crimsonlogic.eventmanagement.service;

import java.util.List;

import com.crimsonlogic.eventmanagement.payload.UserDetailsDto;

public interface UserDetailsService {

	UserDetailsDto getUserDetails(String userDetailsId);

	UserDetailsDto updateUserDetails(UserDetailsDto userDetailsDto);

	void changePassword(String userDetailsId, String currentPassword, String newPassword);

	void addAmountToWallet(String userDetailsId, double amount);

	List<UserDetailsDto> getOrganizers();

	void approveOrganizer(String userDetailsId);

	void rejectOrganizer(String userDetailsId);

	void blockOrganizer(String userDetailsId);

}
