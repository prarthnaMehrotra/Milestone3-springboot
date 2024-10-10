package com.crimsonlogic.eventmanagement.service.test;

import com.crimsonlogic.eventmanagement.entity.Role;
import com.crimsonlogic.eventmanagement.entity.UserAuthentication;
import com.crimsonlogic.eventmanagement.entity.UserDetails;
import com.crimsonlogic.eventmanagement.entity.Wallet;
import com.crimsonlogic.eventmanagement.payload.UserResponseDto;
import com.crimsonlogic.eventmanagement.payload.UserSignInDto;
import com.crimsonlogic.eventmanagement.payload.UserSignUpDto;
import com.crimsonlogic.eventmanagement.repository.RoleRepository;
import com.crimsonlogic.eventmanagement.repository.UserAuthenticationRepository;
import com.crimsonlogic.eventmanagement.repository.UserDetailsRepository;
import com.crimsonlogic.eventmanagement.repository.WalletRepository;
import com.crimsonlogic.eventmanagement.service.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

	@Mock
	private UserAuthenticationRepository userAuthRepo;

	@Mock
	private UserDetailsRepository userDetailsRepo;

	@Mock
	private RoleRepository roleRepo;

	@Mock
	private WalletRepository walletRepo;

	@InjectMocks
	private AuthServiceImpl authService;

	private UserSignUpDto signUpDto;
	private UserSignInDto signInDto;

	@BeforeEach
	void setUp() {
		signUpDto = new UserSignUpDto();
		signUpDto.setEmail("test@example.com");
		signUpDto.setPassword("password");
		signUpDto.setConfirmPassword("password");
		signUpDto.setFirstName("John");
		signUpDto.setLastName("Doe");
		signUpDto.setContactNumber("1234567890");
		signUpDto.setDateOfBirth(LocalDate.parse("1990-01-01"));

		signInDto = new UserSignInDto();
		signInDto.setEmail("test@example.com");
		signInDto.setPassword("password");
	}

	@Test
	void testSignUp_PasswordMismatch() {
		signUpDto.setConfirmPassword("differentPassword");

		Exception exception = assertThrows(RuntimeException.class, () -> authService.signUp(signUpDto));
		assertEquals("Passwords do not match", exception.getMessage());
	}

	@Test
    void testSignUp() {
        UserSignUpDto signUpDto = new UserSignUpDto();
        signUpDto.setEmail("test@example.com");
        signUpDto.setPassword("password");
        signUpDto.setConfirmPassword("password");
        signUpDto.setFirstName("First");
        signUpDto.setLastName("Last");
        signUpDto.setContactNumber("1234567890");
        signUpDto.setAlternateNumber("0987654321");
        signUpDto.setDateOfBirth(LocalDate.parse("1990-01-01"));

        when(userAuthRepo.save(any())).thenReturn(new UserAuthentication());
        when(userDetailsRepo.save(any())).thenReturn(new UserDetails());
        when(roleRepo.save(any())).thenReturn(new Role());
        when(walletRepo.save(any())).thenReturn(new Wallet());

        Object result = authService.signUp(signUpDto);
        assertEquals("User registered successfully!", result);
    }

	@Test
	void testSignIn_Success() {
		UserAuthentication userAuth = new UserAuthentication();
		userAuth.setEmail("test@example.com");
		userAuth.setPassword("password");
		userAuth.setUserId("user123");

		UserDetails userDetails = new UserDetails();
		userDetails.setIsApproved(true); 
		userDetails.setUserDetailsId("details123");
		userDetails.setFullName("John Doe");
		userDetails.setContactNumber("1234567890");
		userDetails.setDateOfBirth(LocalDate.parse("1990-01-01"));

		Role role = new Role();
		role.setRoleName("customer");

		when(userAuthRepo.findByEmail(signInDto.getEmail())).thenReturn(userAuth);
		when(userDetailsRepo.findByDetailsOfUser_UserId(userAuth.getUserId())).thenReturn(userDetails);
		when(roleRepo.findByRoleForUser(userAuth)).thenReturn(role);

		UserResponseDto response = (UserResponseDto) authService.signIn(signInDto);

		assertNotNull(response);
		assertEquals("test@example.com", response.getEmail());
		assertEquals("customer", response.getRole());
	}

	@Test
	void testSignIn_UserNotApproved() {
		UserAuthentication userAuth = new UserAuthentication();
		userAuth.setEmail("test@example.com");
		userAuth.setPassword("password");
		userAuth.setUserId("user123");

		UserDetails userDetails = new UserDetails();
		userDetails.setIsApproved(false);

		when(userAuthRepo.findByEmail(signInDto.getEmail())).thenReturn(userAuth);
		when(userDetailsRepo.findByDetailsOfUser_UserId(userAuth.getUserId())).thenReturn(userDetails);

		Exception exception = assertThrows(RuntimeException.class, () -> authService.signIn(signInDto));
		assertEquals("User is not approved", exception.getMessage());
	}

	@Test
	void testSignIn_InvalidCredentials() {
		when(userAuthRepo.findByEmail(signInDto.getEmail())).thenReturn(null);

		Exception exception = assertThrows(RuntimeException.class, () -> authService.signIn(signInDto));
		assertEquals("Invalid credentials", exception.getMessage());
	}

}
