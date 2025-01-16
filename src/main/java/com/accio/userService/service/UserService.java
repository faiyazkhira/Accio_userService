package com.accio.userService.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accio.userService.dto.UserRequest;
import com.accio.userService.dto.UserResponse;
import com.accio.userService.entity.User;
import com.accio.userService.mapper.UserMapper;
import com.accio.userService.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	// Fetch all users
	public List<UserResponse> getAllUsers() {
		logger.info("Fetching all users from the database");
		List<User> users = userRepository.findAll();

		if (users.isEmpty()) {
			logger.warn("No users found in the database");
		}
		return users.stream().map(UserMapper::toUserResponse).collect(Collectors.toList());
	}

	// Fetch user by ID
	public UserResponse getUserById(Long userId) {
		logger.info("Fetching user by ID: {}", userId);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		return UserMapper.toUserResponse(user);
	}

	// Search user by keyword (username, email, or name)
	public List<UserResponse> searchUserByKeyword(String keyword) {
		logger.info("Searching users by keyword: {}", keyword);
		List<User> users = userRepository.findByUsernameContainingOrEmailContainingOrNameContaining(keyword, keyword,
				keyword);

		if (users.isEmpty()) {
			logger.warn("No users found with the keyword: {}", keyword);
		}
		return users.stream().map(UserMapper::toUserResponse).collect(Collectors.toList());
	}

	// Update user by ID
	@Transactional
	public String updateUserById(Long userId, UserRequest userRequest) {
		logger.info("Updating user with ID: {}", userId);

		if (userRequest.getUsername() == null || userRequest.getUsername().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be null or empty");
		}

		if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty()) {
			throw new IllegalArgumentException("Email cannot be null or empty");
		}

		if (userRequest.getName() == null || userRequest.getName().isEmpty()) {
			throw new IllegalArgumentException("Name cannot be null or empty");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		user.setUsername(userRequest.getUsername());
		user.setEmail(userRequest.getEmail());
		user.setName(userRequest.getName());

		userRepository.save(user);
		logger.info("User with ID: {} updated successfully", userId);

		return "User updated successfully";
	}

	// Delete user by ID
	public String deleteUser(Long userId) {
		logger.info("Deleting user with ID: {}", userId);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		userRepository.delete(user);
		logger.info("User with ID: {} deleted successfully", userId);

		return "User deleted successfully";
	}

}
