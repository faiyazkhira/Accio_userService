package com.accio.userService.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.accio.userService.dto.LoginRequest;
import com.accio.userService.dto.LoginResponse;
import com.accio.userService.dto.SignupRequest;
import com.accio.userService.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("User Service is available now " + new Date(System.currentTimeMillis()));
	}

	@PostMapping("/signin")
	public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		LoginResponse response = authService.authenticateUser(loginRequest);
		return ResponseEntity.ok(response);

	}

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
		return authService.signupUser(signupRequest);

	}

	@PostMapping("/verify-otp")
	public ResponseEntity<Object> verifyOtp(@RequestParam String email, @RequestParam String otp) {
		return authService.verifyOtp(email, otp);
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<String> resendOtp(@RequestParam String email) {
		return authService.resendOtp(email);

	}

}
