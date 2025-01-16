package com.accio.userService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accio.userService.dto.UserRequest;
import com.accio.userService.dto.UserResponse;
import com.accio.userService.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/all")
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		List<UserResponse> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/{userId}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
		UserResponse userResponse = userService.getUserById(userId);
		return ResponseEntity.ok(userResponse);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/search/{keywords}")
	public ResponseEntity<List<UserResponse>> searchUserByKeyword(@PathVariable String keywords) {
		List<UserResponse> users = userService.searchUserByKeyword(keywords);
		return ResponseEntity.ok(users);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{userId}")
	public ResponseEntity<String> updateUserById(@PathVariable Long userId,
			@Valid @RequestBody UserRequest userRequest) {
		String message = userService.updateUserById(userId, userRequest);
		return ResponseEntity.ok(message);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	@DeleteMapping("/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
		String message = userService.deleteUser(userId);
		return ResponseEntity.ok(message);
	}

}
