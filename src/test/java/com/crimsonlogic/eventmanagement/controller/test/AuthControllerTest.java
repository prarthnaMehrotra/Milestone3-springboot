package com.crimsonlogic.eventmanagement.controller.test;

import com.crimsonlogic.eventmanagement.payload.UserSignInDto;
import com.crimsonlogic.eventmanagement.payload.UserSignUpDto;
import com.crimsonlogic.eventmanagement.service.AuthService;
import com.crimsonlogic.eventmanagement.controller.AuthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authService = Mockito.mock(AuthService.class);
        authController = new AuthController();
        authController.authService = authService; 
    }

    @Test
    void testSignIn() {
        UserSignInDto signInDto = new UserSignInDto();
        signInDto.setEmail("test@example.com");
        signInDto.setPassword("password");

        String expectedResponse = "Sign-in successful";
        when(authService.signIn(signInDto)).thenReturn(expectedResponse);

        ResponseEntity<?> response = authController.signIn(signInDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(authService).signIn(signInDto);
    }

    @Test
    void testSignUp() {
        UserSignUpDto signUpDto = new UserSignUpDto();
        signUpDto.setEmail("newuser@example.com");
        signUpDto.setPassword("newpassword");

        String expectedResponse = "Sign-up successful";
        when(authService.signUp(signUpDto)).thenReturn(expectedResponse);

        ResponseEntity<?> response = authController.signUp(signUpDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(authService).signUp(signUpDto);
    }

    @Test
    void testBecomeOrganizer() {
        UserSignUpDto signUpDto = new UserSignUpDto();
        signUpDto.setEmail("organizer@example.com");
        signUpDto.setPassword("organizerpassword");

        String expectedResponse = "Sign-up as organizer successful";
        when(authService.signUpOrganizer(signUpDto)).thenReturn(expectedResponse);

        ResponseEntity<?> response = authController.becomeOrganizer(signUpDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(authService).signUpOrganizer(signUpDto);
    }
}
