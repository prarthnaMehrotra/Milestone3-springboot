package com.crimsonlogic.eventmanagement.controller;

import com.crimsonlogic.eventmanagement.payload.UserDetailsDto;
import com.crimsonlogic.eventmanagement.service.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/userdetails")
@CrossOrigin(origins = "http://localhost:3001")
@Slf4j
public class UserDetailsController {

    @Autowired
    public UserDetailsService userDetailsService; // Service for handling user details logic

    /**
     * Retrieves user details by userDetailsId.
     *
     * @param userDetailsId The ID of the user whose details are to be retrieved.
     * @return A ResponseEntity containing the UserDetailsDto if found, or a 404 if not.
     */
    @GetMapping("/{userDetailsId}")
    public ResponseEntity<UserDetailsDto> getUserDetails(@PathVariable String userDetailsId) {
        UserDetailsDto userDetailsDto = userDetailsService.getUserDetails(userDetailsId);
        if (userDetailsDto != null) {
            return ResponseEntity.ok(userDetailsDto); // Return user details with a 200 status
        }
        return ResponseEntity.notFound().build(); // Return 404 if not found
    }

    /**
     * Updates user details.
     *
     * @param userDetailsDto The DTO containing updated user information.
     * @return A ResponseEntity with the updated UserDetailsDto or a 400 if the update fails.
     */
    @PutMapping("/update")
    public ResponseEntity<UserDetailsDto> updateUserDetails(@RequestBody UserDetailsDto userDetailsDto) {
        UserDetailsDto updatedUserDetails = userDetailsService.updateUserDetails(userDetailsDto);
        if (updatedUserDetails != null) {
            return ResponseEntity.ok(updatedUserDetails); // Return updated details with a 200 status
        }
        return ResponseEntity.badRequest().build(); // Return 400 if update fails
    }

    /**
     * Changes the user's password.
     *
     * @param userDetailsId The ID of the user requesting the password change.
     * @param currentPassword The user's current password.
     * @param newPassword The new password to set.
     * @return A ResponseEntity with a success message or an error message.
     */
    @PutMapping("/{userDetailsId}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable String userDetailsId,
                                                 @RequestParam String currentPassword, 
                                                 @RequestParam String newPassword) {
        log.info("Received password change request for userDetailsId: {}", userDetailsId);
        
        try {
            userDetailsService.changePassword(userDetailsId, currentPassword, newPassword);
            return ResponseEntity.ok("Password changed successfully"); // Return success message
        } catch (Exception e) {
            log.error("Error changing password: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to change password: " + e.getMessage()); // Return error message
        }
    }

    /**
     * Adds an amount to the user's wallet.
     *
     * @param userDetailsId The ID of the user whose wallet is being updated.
     * @param amount A map containing the amount to add to the wallet.
     * @return A ResponseEntity with a success message or an error message.
     */
    @PutMapping("/{userDetailsId}/wallet/add")
    public ResponseEntity<String> addAmountToWallet(@PathVariable String userDetailsId,
                                                     @RequestBody Map<String, Double> amount) {
        try {
            userDetailsService.addAmountToWallet(userDetailsId, amount.get("amount")); // Add the specified amount to the wallet
            return ResponseEntity.ok("Amount added successfully"); // Return success message
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Return error message
        }
    }

    /**
     * Retrieves a list of organizers.
     *
     * @return A ResponseEntity containing a list of UserDetailsDto representing organizers.
     */
    @GetMapping("/organizers")
    public ResponseEntity<List<UserDetailsDto>> getOrganizers() {
        List<UserDetailsDto> organizers = userDetailsService.getOrganizers();
        return ResponseEntity.ok(organizers); // Return list of organizers with a 200 status
    }

    /**
     * Approves a user as an organizer.
     *
     * @param userDetailsId The ID of the user to approve.
     * @return A ResponseEntity with a success message or an error message.
     */
    @PostMapping("/organizers/{userDetailsId}/approve")
    public ResponseEntity<String> approveOrganizer(@PathVariable String userDetailsId) {
        try {
            userDetailsService.approveOrganizer(userDetailsId); // Call service to approve organizer
            return ResponseEntity.ok("Organizer approved successfully"); // Return success message
        } catch (Exception e) {
            log.error("Error approving organizer: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to approve organizer: " + e.getMessage()); // Return error message
        }
    }

    /**
     * Rejects a user as an organizer.
     *
     * @param userDetailsId The ID of the user to reject.
     * @return A ResponseEntity with a success message or an error message.
     */
    @PostMapping("/organizers/{userDetailsId}/reject")
    public ResponseEntity<String> rejectOrganizer(@PathVariable String userDetailsId) {
        try {
            userDetailsService.rejectOrganizer(userDetailsId); // Call service to reject organizer
            return ResponseEntity.ok("Organizer rejected successfully"); // Return success message
        } catch (Exception e) {
            log.error("Error rejecting organizer: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to reject organizer: " + e.getMessage()); // Return error message
        }
    }

    /**
     * Blocks a user from being an organizer.
     *
     * @param userDetailsId The ID of the user to block.
     * @return A ResponseEntity with a success message or an error message.
     */
    @PostMapping("/organizers/{userDetailsId}/block")
    public ResponseEntity<String> blockOrganizer(@PathVariable String userDetailsId) {
        log.info("Blocking organizer with ID: {}", userDetailsId);
        try {
            userDetailsService.blockOrganizer(userDetailsId); // Call service to block organizer
            return ResponseEntity.ok("Organizer blocked successfully"); // Return success message
        } catch (Exception e) {
            log.error("Error blocking organizer {}: {}", userDetailsId, e.getMessage());
            return ResponseEntity.badRequest().body("Failed to block organizer: " + e.getMessage()); // Return error message
        }
    }
}
