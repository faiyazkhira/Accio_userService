package com.accio.userService.service;

import java.util.HashSet;
import java.util.Optional;
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
	AuthenticationManager manager;

	@Autowired
	JwtAuthenticationHelper helper;

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	// signin
	public LoginResponse authenticateUser(LoginRequest loginRequest) {
		// 1. Authenticate with authentication manager

		this.doAuthenticate(loginRequest.getUserNameOrEmail(), loginRequest.getPassword());

		UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUserNameOrEmail());
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
				.email(signupRequest.getEmail()).password(encodedPassword).build();

		Role userRole = roleRepository.findByNameContaining(signupRequest.getRole())
				.orElseThrow(() -> new RuntimeException("Role not found"));

		Set<Role> roles = new HashSet<>();
		roles.add(userRole);
		user.setRoles(roles);

		userRepository.save(user);

		return ResponseEntity.ok("User registered successfully");
	}
}
