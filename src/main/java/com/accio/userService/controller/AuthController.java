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
	public String test() {
		return "User Service is available now " + new Date(System.currentTimeMillis());
	}

	@PostMapping("/signin")
	public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		System.out.println("login req");
		return new ResponseEntity<LoginResponse>(authService.authenticateUser(loginRequest), HttpStatus.OK);

	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/signup")
	public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
		return authService.signupUser(signupRequest);

	}

	@PostMapping("/verify-otp")
	public ResponseEntity<Object> verifyOtp(@RequestParam String email, @RequestParam String otp) {
		return authService.verifyOtp(email, otp);
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<?> resendOtp(@RequestParam String email) {
		// Call the resendOTP method from AuthService
		ResponseEntity<Object> result = authService.resendOtp(email);
		if (result.getStatusCode() == HttpStatus.OK) {
			return ResponseEntity.ok("OTP has been resent successfully.");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to resend OTP. Try again.");
		}
	}

}
