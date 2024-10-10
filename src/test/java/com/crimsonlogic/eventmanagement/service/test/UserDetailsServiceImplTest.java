package com.crimsonlogic.eventmanagement.service.test;

import com.crimsonlogic.eventmanagement.entity.Role;
import com.crimsonlogic.eventmanagement.entity.UserAuthentication;
import com.crimsonlogic.eventmanagement.entity.UserDetails;
import com.crimsonlogic.eventmanagement.entity.Wallet;
import com.crimsonlogic.eventmanagement.payload.UserDetailsDto;
import com.crimsonlogic.eventmanagement.repository.RoleRepository;
import com.crimsonlogic.eventmanagement.repository.UserAuthenticationRepository;
import com.crimsonlogic.eventmanagement.repository.UserDetailsRepository;
import com.crimsonlogic.eventmanagement.repository.WalletRepository;
import com.crimsonlogic.eventmanagement.service.UserDetailsServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private UserDetailsRepository userDetailsRepository;
    private WalletRepository walletRepository;
    private RoleRepository roleRepository;
    private UserAuthenticationRepository userAuthenticationRepository;

    @BeforeEach
    void setUp() {
        userDetailsRepository = Mockito.mock(UserDetailsRepository.class);
        walletRepository = Mockito.mock(WalletRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        userAuthenticationRepository = Mockito.mock(UserAuthenticationRepository.class);

        userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.userDetailsRepository = userDetailsRepository;
        userDetailsService.walletRepository = walletRepository;
        userDetailsService.roleRepository = roleRepository;
        userDetailsService.userAuthenticationRepository = userAuthenticationRepository;
    }

    @Test
    void testGetUserDetails_Success() {
        // Arrange
        String userDetailsId = "user-1";
        UserDetails userDetails = new UserDetails();
        userDetails.setUserDetailsId(userDetailsId);
        userDetails.setFullName("John Doe");
        userDetails.setContactNumber("1234567890");
        userDetails.setAlternateNumber("0987654321");
        userDetails.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userDetails.setIsApproved(true);
        
        UserAuthentication userAuth = new UserAuthentication();
        userAuth.setEmail("john@example.com");
        userDetails.setDetailsOfUser(userAuth);
        
        Wallet wallet = new Wallet();
        wallet.setAmount(100.0);
        
        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.of(userDetails));
        when(walletRepository.findByWalletForUser(userDetails)).thenReturn(wallet);

        // Act
        UserDetailsDto result = userDetailsService.getUserDetails(userDetailsId);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(100.0, result.getWalletAmount());
        verify(userDetailsRepository).findById(userDetailsId);
        verify(walletRepository).findByWalletForUser(userDetails);
    }

    @Test
    void testGetUserDetails_NotFound() {
        // Arrange
        String userDetailsId = "user-1";
        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.empty());

        // Act
        UserDetailsDto result = userDetailsService.getUserDetails(userDetailsId);

        // Assert
        assertNull(result);
        verify(userDetailsRepository).findById(userDetailsId);
    }

    @Test
    void testUpdateUserDetails_Success() {
        // Arrange
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        userDetailsDto.setUserDetailsId("user-1");
        userDetailsDto.setFullName("John Doe Updated");
        userDetailsDto.setContactNumber("1234567890");
        userDetailsDto.setAlternateNumber("0987654321");
        userDetailsDto.setDateOfBirth("1990-01-01");
        userDetailsDto.setIsApproved(true);

        UserDetails userDetails = new UserDetails();
        userDetails.setUserDetailsId("user-1");
        userDetails.setFullName("John Doe");
        userDetails.setContactNumber("1234567890");
        userDetails.setAlternateNumber("0987654321");
        userDetails.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userDetails.setIsApproved(false);

        when(userDetailsRepository.findById(userDetailsDto.getUserDetailsId())).thenReturn(Optional.of(userDetails));

        // Act
        UserDetailsDto result = userDetailsService.updateUserDetails(userDetailsDto);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe Updated", userDetails.getFullName());
        assertTrue(userDetails.getIsApproved());
        verify(userDetailsRepository).save(userDetails);
    }

    @Test
    void testUpdateUserDetails_NotFound() {
        // Arrange
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        userDetailsDto.setUserDetailsId("user-1");

        when(userDetailsRepository.findById(userDetailsDto.getUserDetailsId())).thenReturn(Optional.empty());

        // Act
        UserDetailsDto result = userDetailsService.updateUserDetails(userDetailsDto);

        // Assert
        assertNull(result);
        verify(userDetailsRepository).findById(userDetailsDto.getUserDetailsId());
    }

    @Test
    void testChangePassword_Success() {
        // Arrange
        String userDetailsId = "user-1";
        String currentPassword = "oldPassword";
        String newPassword = "newPassword";

        UserDetails userDetails = new UserDetails();
        UserAuthentication userAuth = new UserAuthentication();
        userAuth.setPassword(currentPassword);
        userDetails.setDetailsOfUser(userAuth);

        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.of(userDetails));

        // Act
        userDetailsService.changePassword(userDetailsId, currentPassword, newPassword);

        // Assert
        assertEquals(newPassword, userAuth.getPassword());
        verify(userAuthenticationRepository).save(userAuth);
    }

    @Test
    void testChangePassword_UserNotFound() {
        // Arrange
        String userDetailsId = "user-1";
        String currentPassword = "oldPassword";
        String newPassword = "newPassword";

        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userDetailsService.changePassword(userDetailsId, currentPassword, newPassword);
        });
        assertEquals("UserDetails not found", exception.getMessage());
    }

    @Test
    void testChangePassword_IncorrectCurrentPassword() {
        // Arrange
        String userDetailsId = "user-1";
        String currentPassword = "oldPassword";
        String newPassword = "newPassword";

        UserDetails userDetails = new UserDetails();
        UserAuthentication userAuth = new UserAuthentication();
        userAuth.setPassword("differentPassword");
        userDetails.setDetailsOfUser(userAuth);

        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.of(userDetails));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userDetailsService.changePassword(userDetailsId, currentPassword, newPassword);
        });
        assertEquals("Current password is incorrect", exception.getMessage());
    }

    @Test
    void testAddAmountToWallet_Success() {
        // Arrange
        String userDetailsId = "user-1";
        double amountToAdd = 50.0;

        UserDetails userDetails = new UserDetails();
        userDetails.setUserDetailsId(userDetailsId);

        Wallet wallet = new Wallet();
        wallet.setAmount(100.0);

        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.of(userDetails));
        when(walletRepository.findByWalletForUser(userDetails)).thenReturn(wallet);

        // Act
        userDetailsService.addAmountToWallet(userDetailsId, amountToAdd);

        // Assert
        assertEquals(150.0, wallet.getAmount());
        verify(walletRepository).save(wallet);
    }

    @Test
    void testAddAmountToWallet_UserNotFound() {
        // Arrange
        String userDetailsId = "user-1";
        double amountToAdd = 50.0;

        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userDetailsService.addAmountToWallet(userDetailsId, amountToAdd);
        });
        assertEquals("User not found: user-1", exception.getMessage());
    }

    @Test
    void testAddAmountToWallet_WalletNotFound() {
        // Arrange
        String userDetailsId = "user-1";
        double amountToAdd = 50.0;

        UserDetails userDetails = new UserDetails();
        userDetails.setUserDetailsId(userDetailsId);

        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.of(userDetails));
        when(walletRepository.findByWalletForUser(userDetails)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userDetailsService.addAmountToWallet(userDetailsId, amountToAdd);
        });
        assertEquals("Wallet not found for user: user-1", exception.getMessage());
    }

    @Test
    void testGetOrganizers() {
        // Arrange
        UserDetails userDetails = new UserDetails();
        UserAuthentication userAuth = new UserAuthentication();
        userAuth.setEmail("organizer@example.com");
        userDetails.setDetailsOfUser(userAuth);
        userDetails.setUserDetailsId("user-1");
        userDetails.setFullName("Organizer User");
        userDetails.setIsApproved(true);
        
        Role role = new Role();
        role.setRoleName("organizer");

        when(userDetailsRepository.findAll()).thenReturn(List.of(userDetails));
        when(roleRepository.findByRoleForUser(userAuth)).thenReturn(role);

        // Act
        List<UserDetailsDto> result = userDetailsService.getOrganizers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Organizer User", result.get(0).getFullName());
        assertEquals("organizer@example.com", result.get(0).getEmail());
    }

    @Test
    void testApproveOrganizer() {
        // Arrange
        String userDetailsId = "user-1";
        UserDetails userDetails = new UserDetails();
        userDetails.setUserDetailsId(userDetailsId);
        userDetails.setIsApproved(false);

        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.of(userDetails));

        // Act
        userDetailsService.approveOrganizer(userDetailsId);

        // Assert
        assertTrue(userDetails.getIsApproved());
        verify(userDetailsRepository).save(userDetails);
    }

    @Test
    void testRejectOrganizer() {
        // Arrange
        String userDetailsId = "user-1";
        UserDetails userDetails = new UserDetails();
        userDetails.setUserDetailsId(userDetailsId);
        userDetails.setIsApproved(true);

        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.of(userDetails));

        // Act
        userDetailsService.rejectOrganizer(userDetailsId);

        // Assert
        assertFalse(userDetails.getIsApproved());
        verify(userDetailsRepository).save(userDetails);
    }

    @Test
    void testBlockOrganizer() {
        // Arrange
        String userDetailsId = "user-1";
        UserDetails userDetails = new UserDetails();
        userDetails.setUserDetailsId(userDetailsId);
        userDetails.setIsApproved(true);

        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.of(userDetails));

        // Act
        userDetailsService.blockOrganizer(userDetailsId);

        // Assert
        assertFalse(userDetails.getIsApproved());
        verify(userDetailsRepository).save(userDetails);
    }
}
