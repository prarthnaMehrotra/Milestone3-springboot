package com.crimsonlogic.eventmanagement.service;

import com.crimsonlogic.eventmanagement.payload.UserSignInDto;
import com.crimsonlogic.eventmanagement.payload.UserSignUpDto;

public interface AuthService {
	
	Object signIn(UserSignInDto signInDto);

	Object signUp(UserSignUpDto signUpDto);

	Object signUpOrganizer(UserSignUpDto signUpDto);
	
}