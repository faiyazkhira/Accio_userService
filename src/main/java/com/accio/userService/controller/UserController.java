package com.accio.userService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/all")
	public List<UserResponse> getAllUsers() {
		return userService.getAllUsers();
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/{userId}")
	public UserResponse getUserById(@PathVariable Long userId) {
		return userService.getUserById(userId);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/search/{keywords}")
	public List<UserResponse> searchUserByKeyword(@PathVariable String keywords) {
		return userService.searchUserByKeyword(keywords);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{userId}")
	public String updateUserById(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
		return userService.updateUserById(userId, userRequest);
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{userId}")
	public String deleteUser(@PathVariable Long userId) {
		return userService.deleteUser(userId);
	}

}
