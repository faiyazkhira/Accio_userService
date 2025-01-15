package com.accio.userService.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

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
import com.accio.userService.repository.RoleRepository;
import com.accio.userService.repository.UserRepository;
import com.accio.userService.security.JwtAuthenticationHelper;

@Service
public class AuthService {

	@Autowired
	private AuthenticationManager manager;

	@Autowired
	private JwtAuthenticationHelper helper;

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

	// signin
	public LoginResponse authenticateUser(LoginRequest loginRequest) {
		// 1. Authenticate with authentication manager

		this.doAuthenticate(loginRequest.getUserNameOrEmail(), loginRequest.getPassword());

		UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUserNameOrEmail());
		User user = userRepository
				.findByUsernameOrEmail(loginRequest.getUserNameOrEmail(), loginRequest.getUserNameOrEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!user.getIsVerified()) {
			throw new RuntimeException("User not verified. Please verify your email");
		}

		String token = helper.generateToken(userDetails);

		String username = userDetails.getUsername();
		String name = ((User) userDetails).getName();
		String email = ((User) userDetails).getEmail();
		String id = String.valueOf(((User) userDetails).getId());

		LoginResponse response = LoginResponse.builder().token(token).username(username).name(name).email(email).id(id)
				.build();
//		String username = userDetails.getUsername();
//
//		LoginResponse response = LoginResponse.builder().token(token).build();

		return response;
	}

	private void doAuthenticate(String userNameOrEmail, String password) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				userNameOrEmail, password);

		try {
			manager.authenticate(authenticationToken);
		} catch (BadCredentialsException e) {
			System.out.println("Failed login");
			throw new BadCredentialsException("Invalid username or password");
		}
	}

	// signup
	public ResponseEntity<Object> signupUser(SignupRequest signupRequest) {
		Optional<User> existsByUsername = userRepository.findByUsername(signupRequest.getUsername());
		Optional<User> existsByEmail = userRepository.findByEmail(signupRequest.getEmail());
		if (existsByUsername.isPresent() || existsByEmail.isPresent()) {
			return ResponseEntity.badRequest().body("User already exists");
		}

		String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

		User user = User.builder().username(signupRequest.getUsername()).name(signupRequest.getName())
				.email(signupRequest.getEmail()).password(encodedPassword).isVerified(false).build();

		// Generate Otp
		String otp = generateOtp();
		user.setOtp(otp);
		user.setOtpexpiry(LocalDateTime.now().plusMinutes(10));

		Role userRole = roleRepository.findByNameContaining(signupRequest.getRole())
				.orElseThrow(() -> new RuntimeException("Role not found"));

		Set<Role> roles = new HashSet<>();
		roles.add(userRole);
		user.setRoles(roles);

		userRepository.save(user);

		// Send Email
		emailService.sendEmail(user.getEmail(), "OTP verification", "Your OTP is: " + otp);

		return ResponseEntity.ok("User registered successfully. Please verify your email using the OTP.");
	}

	private String generateOtp() {
		return String.valueOf(new Random().nextInt(900000) + 100000);
	}

	public ResponseEntity<Object> verifyOtp(String email, String otp) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		if (user.getOtp() == null || user.getOtpexpiry() == null || user.getOtpexpiry().isBefore(LocalDateTime.now())) {
			return ResponseEntity.badRequest().body("OTP has expired");
		}

		if (!user.getOtp().equals(otp)) {
			return ResponseEntity.badRequest().body("Invalid OTP");
		}

		user.setIsVerified(true);
		user.setOtp(null);
		user.setOtpexpiry(null);
		userRepository.save(user);

		return ResponseEntity.ok("User verified successfully");
	}

	public ResponseEntity<Object> resendOtp(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		String newOtp = generateOtp();
		user.setOtp(newOtp);
		user.setOtpexpiry(LocalDateTime.now().plusMinutes(10));
		userRepository.save(user);

		emailService.sendEmail(newOtp, "Resend OTP", "Your new OTP is: " + newOtp);

		return ResponseEntity.ok("New OTP sent successfully");
	}
}
