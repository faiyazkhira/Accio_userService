package com.accio.userService.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.accio.userService.dto.LoginRequest;
import com.accio.userService.dto.LoginResponse;
import com.accio.userService.dto.SignupRequest;
import com.accio.userService.entity.Role;
import com.accio.userService.entity.User;
import com.accio.userService.exception.UserNotFoundException;
import com.accio.userService.repository.RoleRepository;
import com.accio.userService.repository.UserRepository;
import com.accio.userService.security.JwtAuthenticationHelper;

@Service
public class AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtAuthenticationHelper jwtHelper;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private EmailService emailService;

	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

	// Login user
	public LoginResponse authenticateUser(LoginRequest loginRequest) {
		logger.info("Authenticating user");
		this.authenticate(loginRequest.getUserNameOrEmail(), loginRequest.getPassword());

		UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUserNameOrEmail());
		User user = userRepository
				.findByUsernameOrEmail(loginRequest.getUserNameOrEmail(), loginRequest.getUserNameOrEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));
		logger.info("User Authenticated");

		if (!Boolean.TRUE.equals(user.getIsVerified())) {
			logger.info("User not verified");
			throw new RuntimeException("User not verified. Please verify your email");
		}

		String token = jwtHelper.generateToken(userDetails);

		return LoginResponse.builder().token(token).username(user.getUsername()).name(user.getName())
				.email(user.getEmail()).id(String.valueOf(user.getId())).build();
	}

	private void authenticate(String userNameOrEmail, String password) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userNameOrEmail, password));
		} catch (BadCredentialsException ex) {
			throw new BadCredentialsException("Invalid username or password", ex);
		}
	}

	// Register user
	public ResponseEntity<Object> signupUser(SignupRequest signupRequest) {
		if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()
				|| userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
			return ResponseEntity.badRequest().body("User already exists");
		}

		String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

		User user = User.builder().username(signupRequest.getUsername()).name(signupRequest.getName())
				.email(signupRequest.getEmail()).password(encodedPassword).isVerified(false).otp(generateOtp())
				.otpexpiry(LocalDateTime.now().plusMinutes(10)).build();

		Role role = roleRepository.findByNameContaining(signupRequest.getRole())
				.orElseThrow(() -> new RuntimeException("Role not found"));

		Set<Role> roles = new HashSet<>();
		roles.add(role);
		user.setRoles(roles);
		userRepository.save(user);

		emailService.sendEmail(user.getEmail(), "OTP verification", "Your OTP is: " + user.getOtp());
		return ResponseEntity.ok("User registered successfully. Please verify your email using the OTP.");
	}

	private String generateOtp() {
		return String.format("%06d", new Random().nextInt(999999));
	}

	// Verify OTP
	public ResponseEntity<Object> verifyOtp(String email, String otp) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		if (user.getOtp() == null || user.getOtpexpiry() == null || user.getOtpexpiry().isBefore(LocalDateTime.now())) {
			return ResponseEntity.badRequest().body("OTP has expired");
		}

		if (!otp.equals(user.getOtp())) {
			return ResponseEntity.badRequest().body("Invalid OTP");
		}

		user.setIsVerified(true);
		user.setOtp(null);
		user.setOtpexpiry(null);
		userRepository.save(user);

		return ResponseEntity.ok("User verified successfully");
	}

	// Resend OTP
	public ResponseEntity<String> resendOtp(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

		if (Boolean.FALSE.equals(user.getIsVerified())) {
			String newOtp = generateOtp();
			user.setOtp(newOtp);
			user.setOtpexpiry(LocalDateTime.now().plusMinutes(10));
			userRepository.save(user);
			emailService.sendEmail(email, "Resend OTP", "Your new OTP is: " + newOtp);
			return ResponseEntity.ok("New OTP sent successfully");
		} else {
			return ResponseEntity.badRequest().body("User is already verified");
		}

	}
}
