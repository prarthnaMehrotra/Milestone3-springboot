package com.crimsonlogic.eventmanagement.controller.test;

import com.crimsonlogic.eventmanagement.controller.UserDetailsController;
import com.crimsonlogic.eventmanagement.payload.UserDetailsDto;
import com.crimsonlogic.eventmanagement.service.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsControllerTest {

	private UserDetailsService userDetailsService;
	private UserDetailsController userDetailsController;

	@BeforeEach
	void setUp() {
		userDetailsService = Mockito.mock(UserDetailsService.class);
		userDetailsController = new UserDetailsController();
		userDetailsController.userDetailsService = userDetailsService;
	}

	@Test
	void testGetUserDetails() {
		String userDetailsId = "user-1";
		UserDetailsDto userDetailsDto = new UserDetailsDto();
		userDetailsDto.setUserDetailsId(userDetailsId);
		userDetailsDto.setFullName("Test User");

		when(userDetailsService.getUserDetails(userDetailsId)).thenReturn(userDetailsDto);

		ResponseEntity<UserDetailsDto> response = userDetailsController.getUserDetails(userDetailsId);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals(userDetailsDto, response.getBody());
		verify(userDetailsService).getUserDetails(userDetailsId);
	}

	@Test
	void testGetUserDetails_NotFound() {
		String userDetailsId = "user-1";
		when(userDetailsService.getUserDetails(userDetailsId)).thenReturn(null);

		ResponseEntity<UserDetailsDto> response = userDetailsController.getUserDetails(userDetailsId);

		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	void testUpdateUserDetails() {
		UserDetailsDto userDetailsDto = new UserDetailsDto();
		userDetailsDto.setUserDetailsId("user-1");
		userDetailsDto.setFullName("Updated User");

		when(userDetailsService.updateUserDetails(userDetailsDto)).thenReturn(userDetailsDto);

		ResponseEntity<UserDetailsDto> response = userDetailsController.updateUserDetails(userDetailsDto);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals(userDetailsDto, response.getBody());
		verify(userDetailsService).updateUserDetails(userDetailsDto);
	}

	@Test
	void testUpdateUserDetails_BadRequest() {
		UserDetailsDto userDetailsDto = new UserDetailsDto();
		userDetailsDto.setUserDetailsId("user-1");

		when(userDetailsService.updateUserDetails(userDetailsDto)).thenReturn(null);

		ResponseEntity<UserDetailsDto> response = userDetailsController.updateUserDetails(userDetailsDto);

		assertEquals(400, response.getStatusCodeValue());
	}

	@Test
	void testChangePassword() {
		String userDetailsId = "user-1";
		String currentPassword = "oldPassword";
		String newPassword = "newPassword";

		ResponseEntity<String> response = userDetailsController.changePassword(userDetailsId, currentPassword,
				newPassword);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals("Password changed successfully", response.getBody());
		verify(userDetailsService).changePassword(userDetailsId, currentPassword, newPassword);
	}

	@Test
	void testChangePassword_Exception() {
		String userDetailsId = "user-1";
		String currentPassword = "oldPassword";
		String newPassword = "newPassword";
		doThrow(new RuntimeException("Error changing password")).when(userDetailsService).changePassword(userDetailsId,
				currentPassword, newPassword);

		ResponseEntity<String> response = userDetailsController.changePassword(userDetailsId, currentPassword,
				newPassword);

		assertEquals(400, response.getStatusCodeValue());
		assertEquals("Failed to change password: Error changing password", response.getBody());
	}

	@Test
	void testAddAmountToWallet() {
		String userDetailsId = "user-1";
		Map<String, Double> amount = new HashMap<>();
		amount.put("amount", 100.0);

		ResponseEntity<String> response = userDetailsController.addAmountToWallet(userDetailsId, amount);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals("Amount added successfully", response.getBody());
		verify(userDetailsService).addAmountToWallet(userDetailsId, 100.0);
	}

	@Test
	void testAddAmountToWallet_Exception() {
		String userDetailsId = "user-1";
		Map<String, Double> amount = new HashMap<>();
		amount.put("amount", 100.0);
		doThrow(new RuntimeException("Error adding amount")).when(userDetailsService).addAmountToWallet(userDetailsId,
				100.0);

		ResponseEntity<String> response = userDetailsController.addAmountToWallet(userDetailsId, amount);

		assertEquals(400, response.getStatusCodeValue());
		assertEquals("Error adding amount", response.getBody());
	}

	@Test
	void testGetOrganizers() {
		List<UserDetailsDto> organizers = Collections.singletonList(new UserDetailsDto());
		when(userDetailsService.getOrganizers()).thenReturn(organizers);

		ResponseEntity<List<UserDetailsDto>> response = userDetailsController.getOrganizers();

		assertEquals(200, response.getStatusCodeValue());
		assertEquals(organizers, response.getBody());
	}

	@Test
	void testApproveOrganizer() {
		String userDetailsId = "user-1";

		ResponseEntity<String> response = userDetailsController.approveOrganizer(userDetailsId);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals("Organizer approved successfully", response.getBody());
		verify(userDetailsService).approveOrganizer(userDetailsId);
	}

	@Test
	void testRejectOrganizer() {
		String userDetailsId = "user-1";

		ResponseEntity<String> response = userDetailsController.rejectOrganizer(userDetailsId);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals("Organizer rejected successfully", response.getBody());
		verify(userDetailsService).rejectOrganizer(userDetailsId);
	}

	@Test
	void testBlockOrganizer() {
		String userDetailsId = "user-1";

		ResponseEntity<String> response = userDetailsController.blockOrganizer(userDetailsId);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals("Organizer blocked successfully", response.getBody());
		verify(userDetailsService).blockOrganizer(userDetailsId);
	}
}
