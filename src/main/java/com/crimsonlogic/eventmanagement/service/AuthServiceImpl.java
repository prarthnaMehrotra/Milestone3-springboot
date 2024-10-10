package com.crimsonlogic.eventmanagement.service;

import com.crimsonlogic.eventmanagement.entity.Role;
import com.crimsonlogic.eventmanagement.entity.UserAuthentication;
import com.crimsonlogic.eventmanagement.entity.UserDetails;
import com.crimsonlogic.eventmanagement.entity.Wallet;
import com.crimsonlogic.eventmanagement.exception.InvalidCredentialsException;
import com.crimsonlogic.eventmanagement.exception.PasswordsDoNotMatchException;
import com.crimsonlogic.eventmanagement.exception.UserNotApprovedException;
import com.crimsonlogic.eventmanagement.exception.UserNotFoundException;
import com.crimsonlogic.eventmanagement.payload.UserResponseDto;
import com.crimsonlogic.eventmanagement.payload.UserSignInDto;
import com.crimsonlogic.eventmanagement.payload.UserSignUpDto;
import com.crimsonlogic.eventmanagement.repository.RoleRepository;
import com.crimsonlogic.eventmanagement.repository.UserAuthenticationRepository;
import com.crimsonlogic.eventmanagement.repository.UserDetailsRepository;
import com.crimsonlogic.eventmanagement.repository.WalletRepository;
import com.crimsonlogic.eventmanagement.util.IDGenerator;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserAuthenticationRepository userAuthRepo;

    @Autowired
    private UserDetailsRepository userDetailsRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private WalletRepository walletRepo;

    /**
     * Handles user sign-in functionality.
     *
     * @param signInDto Data Transfer Object containing sign-in information.
     * @return UserResponseDto containing user information if sign-in is successful.
     * @throws UserNotFoundException if the user is not found.
     * @throws InvalidCredentialsException if the password is incorrect.
     * @throws UserNotApprovedException if the user is not approved.
     */
    @Override
    public Object signIn(UserSignInDto signInDto) {
        log.info("Attempting to sign in with email: {}", signInDto.getEmail());
        UserAuthentication user = userAuthRepo.findByEmail(signInDto.getEmail());

        // Check if user exists
        if (user != null) {
            log.info("User found: {}", user);
            // Validate password
            if (user.getPassword().equals(signInDto.getPassword())) {
                UserDetails userDetails = userDetailsRepo.findByDetailsOfUser_UserId(user.getUserId());
                Role role = roleRepo.findByRoleForUser(user);

                // Check if user details exist
                if (userDetails != null) {
                    // Check if user is approved
                    if (userDetails.getIsApproved()) {
                        log.info("User details: {}", userDetails);
                        return new UserResponseDto(user.getEmail(), role.getRoleName(), user.getUserId(),
                                userDetails.getUserDetailsId());
                    } else {
                        log.warn("User is not approved for email: {}", user.getEmail());
                        throw new UserNotApprovedException("User is not approved");
                    }
                } else {
                    log.warn("User details not found for userId: {}", user.getUserId());
                    throw new UserNotFoundException("User details not found");
                }
            } else {
                log.warn("Invalid password for user: {}", user.getEmail());
                throw new InvalidCredentialsException("Invalid password");
            }
        } else {
            log.warn("User not found with email: {}", signInDto.getEmail());
            throw new UserNotFoundException("User not found with the provided email");
        }
    }

    /**
     * Handles user sign-up functionality.
     *
     * @param signUpDto Data Transfer Object containing sign-up information.
     * @return A success message if registration is successful.
     * @throws PasswordsDoNotMatchException if passwords do not match.
     */
    @Override
    public Object signUp(UserSignUpDto signUpDto) {
        // Check if passwords match
        if (!signUpDto.getPassword().equals(signUpDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException("Passwords do not match");
        }

        // Create new user authentication entry
        UserAuthentication userAuth = new UserAuthentication();
        userAuth.setUserId(IDGenerator.generateUserID());
        userAuth.setEmail(signUpDto.getEmail());
        userAuth.setPassword(signUpDto.getPassword());
        userAuthRepo.save(userAuth);

        // Create new user details entry
        UserDetails userDetails = new UserDetails();
        userDetails.setUserDetailsId(IDGenerator.generateUserDetailID());
        userDetails.setFullName(signUpDto.getFirstName() + " " + signUpDto.getLastName());
        userDetails.setContactNumber(signUpDto.getContactNumber());
        userDetails.setAlternateNumber(signUpDto.getAlternateNumber());
        userDetails.setDateOfBirth(signUpDto.getDateOfBirth());
        userDetails.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userDetails.setIsApproved(true); // Automatically approved for standard users
        userDetails.setDetailsOfUser(userAuth);
        userDetailsRepo.save(userDetails);

        // Assign default role to user
        Role role = new Role();
        role.setRoleId(IDGenerator.generateRoleID());
        role.setRoleName("customer"); // Default role
        role.setRoleForUser(userAuth);
        roleRepo.save(role);

        // Create wallet for the user
        Wallet wallet = new Wallet();
        wallet.setWalletId(IDGenerator.generateWalletID());
        wallet.setWalletForUser(userDetails);
        wallet.setAmount(0); // Initialize with zero balance
        walletRepo.save(wallet);

        return "User registered successfully!";
    }

    /**
     * Handles organizer sign-up functionality.
     *
     * @param signUpDto Data Transfer Object containing sign-up information for organizers.
     * @return A success message if registration is successful.
     * @throws PasswordsDoNotMatchException if passwords do not match.
     */
    @Override
    public Object signUpOrganizer(UserSignUpDto signUpDto) {
        // Check if passwords match
        if (!signUpDto.getPassword().equals(signUpDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException("Passwords do not match");
        }

        // Create new user authentication entry
        UserAuthentication userAuth = new UserAuthentication();
        userAuth.setUserId(IDGenerator.generateUserID());
        userAuth.setEmail(signUpDto.getEmail());
        userAuth.setPassword(signUpDto.getPassword());
        userAuthRepo.save(userAuth);

        // Create new user details entry
        UserDetails userDetails = new UserDetails();
        userDetails.setUserDetailsId(IDGenerator.generateUserDetailID());
        userDetails.setFullName(signUpDto.getFirstName() + " " + signUpDto.getLastName());
        userDetails.setContactNumber(signUpDto.getContactNumber());
        userDetails.setAlternateNumber(signUpDto.getAlternateNumber());
        userDetails.setDateOfBirth(signUpDto.getDateOfBirth());
        userDetails.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userDetails.setIsApproved(null); // Pending approval for organizers
        userDetails.setDetailsOfUser(userAuth);
        userDetailsRepo.save(userDetails);

        // Assign role of organizer
        Role role = new Role();
        role.setRoleId(IDGenerator.generateRoleID());
        role.setRoleName("organizer");
        role.setRoleForUser(userAuth);
        roleRepo.save(role);

        return "Organizer registered successfully!";
    }

}
