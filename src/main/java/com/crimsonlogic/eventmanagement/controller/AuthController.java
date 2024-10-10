package com.crimsonlogic.eventmanagement.controller;

import com.crimsonlogic.eventmanagement.payload.UserSignInDto;
import com.crimsonlogic.eventmanagement.payload.UserSignUpDto;
import com.crimsonlogic.eventmanagement.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3001")
public class AuthController {

    @Autowired
    public AuthService authService; // Service for handling authentication logic

    /**
     * Handles user sign-in requests.
     *
     * @param signInDto The data transfer object containing sign-in credentials.
     * @return A ResponseEntity containing the sign-in response.
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody UserSignInDto signInDto) {
        // Delegate sign-in logic to the AuthService and return the response
        return ResponseEntity.ok(authService.signIn(signInDto));
    }

    /**
     * Handles user sign-up requests.
     *
     * @param signUpDto The data transfer object containing user registration information.
     * @return A ResponseEntity containing the sign-up response.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserSignUpDto signUpDto) {
        // Delegate sign-up logic to the AuthService and return the response
        return ResponseEntity.ok(authService.signUp(signUpDto));
    }

    /**
     * Handles requests for users to become organizers.
     *
     * @param signUpDto The data transfer object containing organizer registration information.
     * @return A ResponseEntity containing the response for becoming an organizer.
     */
    @PostMapping("/become-organizer")
    public ResponseEntity<?> becomeOrganizer(@RequestBody UserSignUpDto signUpDto) {
        // Delegate the logic for becoming an organizer to the AuthService and return the response
        return ResponseEntity.ok(authService.signUpOrganizer(signUpDto));
    }

}
