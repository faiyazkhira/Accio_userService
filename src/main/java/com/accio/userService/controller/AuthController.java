package com.accio.userService.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.accio.userService.dto.LoginRequest;
import com.accio.userService.dto.LoginResponse;
import com.accio.userService.dto.SignupRequest;
import com.accio.userService.service.AuthService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Value("${jwt.secret}")
	private String secretKey;

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

	@PostMapping("/validate-token")
	public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC512(secretKey);
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT jwt = verifier.verify(token.replace("Bearer ", ""));
			return ResponseEntity.ok(jwt.getClaim("email").asString());
		} catch (JWTVerificationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}
	}

}
