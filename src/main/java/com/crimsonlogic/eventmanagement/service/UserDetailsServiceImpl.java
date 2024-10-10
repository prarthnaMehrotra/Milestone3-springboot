package com.crimsonlogic.eventmanagement.service;

import com.crimsonlogic.eventmanagement.payload.UserDetailsDto;
import com.crimsonlogic.eventmanagement.entity.Role;
import com.crimsonlogic.eventmanagement.entity.UserAuthentication;
import com.crimsonlogic.eventmanagement.entity.UserDetails;
import com.crimsonlogic.eventmanagement.entity.Wallet;
import com.crimsonlogic.eventmanagement.exception.CurrentPasswordIncorrectException;
import com.crimsonlogic.eventmanagement.exception.InvalidAmountException;
import com.crimsonlogic.eventmanagement.exception.UserNotFoundException;
import com.crimsonlogic.eventmanagement.exception.WalletNotFoundException;
import com.crimsonlogic.eventmanagement.repository.RoleRepository;
import com.crimsonlogic.eventmanagement.repository.UserAuthenticationRepository;
import com.crimsonlogic.eventmanagement.repository.UserDetailsRepository;
import com.crimsonlogic.eventmanagement.repository.WalletRepository;
import com.crimsonlogic.eventmanagement.service.UserDetailsService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    public UserDetailsRepository userDetailsRepository;

    @Autowired
    public WalletRepository walletRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    public UserAuthenticationRepository userAuthenticationRepository;

    /**
     * Retrieves user details by userDetailsId.
     *
     * @param userDetailsId The ID of the user whose details are to be retrieved.
     * @return UserDetailsDto containing user details.
     * @throws UserNotFoundException if the user is not found.
     */
    @Override
    public UserDetailsDto getUserDetails(String userDetailsId) {
        UserDetails userDetails = userDetailsRepository.findById(userDetailsId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userDetailsId));

        Wallet wallet = walletRepository.findByWalletForUser(userDetails);
        double walletAmount = (wallet != null) ? wallet.getAmount() : 0.0;

        return new UserDetailsDto(userDetails.getUserDetailsId(), userDetails.getFullName(),
                userDetails.getContactNumber(), userDetails.getAlternateNumber(),
                userDetails.getDateOfBirth().toString(), userDetails.getIsApproved(),
                userDetails.getDetailsOfUser().getEmail(), walletAmount);
    }

    /**
     * Updates user details.
     *
     * @param userDetailsDto The DTO containing updated user details.
     * @return The updated UserDetailsDto.
     * @throws UserNotFoundException if the user is not found.
     */
    @Override
    public UserDetailsDto updateUserDetails(UserDetailsDto userDetailsDto) {
        UserDetails userDetails = userDetailsRepository.findById(userDetailsDto.getUserDetailsId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userDetailsDto.getUserDetailsId()));

        // Update user details fields
        userDetails.setFullName(userDetailsDto.getFullName());
        userDetails.setContactNumber(userDetailsDto.getContactNumber());
        userDetails.setAlternateNumber(userDetailsDto.getAlternateNumber());

        // Parse and set date of birth if provided
        if (userDetailsDto.getDateOfBirth() != null && !userDetailsDto.getDateOfBirth().isEmpty()) {
            userDetails.setDateOfBirth(LocalDate.parse(userDetailsDto.getDateOfBirth()));
        }

        // Mark user as approved and update creation timestamp
        userDetails.setIsApproved(true);
        userDetails.setCreatedAt(Timestamp.valueOf(LocalDate.now().atStartOfDay()));

        userDetailsRepository.save(userDetails);
        return userDetailsDto;
    }

    /**
     * Changes the user's password.
     *
     * @param userDetailsId The ID of the user whose password is to be changed.
     * @param currentPassword The current password of the user.
     * @param newPassword The new password to be set.
     * @throws UserNotFoundException if the user is not found.
     * @throws CurrentPasswordIncorrectException if the current password is incorrect.
     */
    @Override
    public void changePassword(String userDetailsId, String currentPassword, String newPassword) {
        log.info("Changing password for userDetailsId: {}", userDetailsId);

        UserDetails userDetails = userDetailsRepository.findById(userDetailsId)
                .orElseThrow(() -> new UserNotFoundException("UserDetails not found: " + userDetailsId));

        UserAuthentication userAuth = userDetails.getDetailsOfUser();

        // Verify current password
        if (!userAuth.getPassword().equals(currentPassword)) {
            throw new CurrentPasswordIncorrectException("Current password is incorrect");
        }

        // Set the new password and save
        userAuth.setPassword(newPassword);
        userAuthenticationRepository.save(userAuth);

        log.info("Password changed successfully for userDetailsId: {}", userDetailsId);
    }

    /**
     * Adds a specified amount to the user's wallet.
     *
     * @param userDetailsId The ID of the user whose wallet amount is to be updated.
     * @param amount The amount to be added to the wallet.
     * @throws UserNotFoundException if the user is not found.
     * @throws WalletNotFoundException if the wallet is not found.
     * @throws InvalidAmountException if the amount is not greater than zero.
     */
    @Override
    public void addAmountToWallet(String userDetailsId, double amount) {
        UserDetails userDetails = userDetailsRepository.findById(userDetailsId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userDetailsId));

        Wallet wallet = walletRepository.findByWalletForUser(userDetails);
        if (wallet == null) {
            throw new WalletNotFoundException("Wallet not found for user: " + userDetailsId);
        }

        // Validate the amount
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        // Update wallet amount and save
        wallet.setAmount(wallet.getAmount() + amount);
        walletRepository.save(wallet);
    }

    /**
     * Retrieves a list of organizers.
     *
     * @return List of UserDetailsDto for all organizers.
     */
    @Override
    public List<UserDetailsDto> getOrganizers() {
        List<UserDetails> allUserDetails = userDetailsRepository.findAll();
        List<UserDetailsDto> organizers = new ArrayList<>();

        // Iterate through user details to find organizers
        for (UserDetails userDetails : allUserDetails) {
            UserAuthentication userAuth = userDetails.getDetailsOfUser();
            if (userAuth != null) {
                Role role = roleRepository.findByRoleForUser(userAuth);
                if (role != null && "organizer".equals(role.getRoleName())) {
                    UserDetailsDto dto = new UserDetailsDto(userDetails.getUserDetailsId(), userDetails.getFullName(),
                            userDetails.getContactNumber(), userDetails.getAlternateNumber(),
                            userDetails.getDateOfBirth() != null ? userDetails.getDateOfBirth().toString() : null,
                            userDetails.getIsApproved(), userAuth.getEmail(), 0.0);
                    organizers.add(dto);
                }
            }
        }

        return organizers;
    }

    /**
     * Approves an organizer.
     *
     * @param userDetailsId The ID of the organizer to be approved.
     * @throws UserNotFoundException if the user is not found.
     */
    @Override
    public void approveOrganizer(String userDetailsId) {
        UserDetails userDetails = userDetailsRepository.findById(userDetailsId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userDetailsId));

        // Set user as approved and save
        userDetails.setIsApproved(true);
        log.info("Approving user: {}", userDetails);
        userDetailsRepository.save(userDetails);
    }

    /**
     * Rejects an organizer.
     *
     * @param userDetailsId The ID of the organizer to be rejected.
     * @throws UserNotFoundException if the user is not found.
     */
    @Override
    public void rejectOrganizer(String userDetailsId) {
        UserDetails userDetails = userDetailsRepository.findById(userDetailsId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userDetailsId));

        // Set user as not approved and save
        userDetails.setIsApproved(false);
        log.info("Rejecting user: {}", userDetails);
        userDetailsRepository.save(userDetails);
    }

    /**
     * Blocks an organizer.
     *
     * @param userDetailsId The ID of the organizer to be blocked.
     * @throws UserNotFoundException if the user is not found.
     */
    @Override
    public void blockOrganizer(String userDetailsId) {
        UserDetails userDetails = userDetailsRepository.findById(userDetailsId)
                .orElseThrow(() -> new UserNotFoundException("Organizer not found: " + userDetailsId));

        // Set user as not approved and save
        userDetails.setIsApproved(false);
        log.info("Blocking user: {}", userDetails);
        userDetailsRepository.save(userDetails);
    }
}
