package com.accio.userService.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accio.userService.dto.UserRequest;
import com.accio.userService.dto.UserResponse;
import com.accio.userService.entity.User;
import com.accio.userService.mapper.UserMapper;
import com.accio.userService.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public List<UserResponse> getAllUsers() {
		List<User> users = userRepository.findAll();

		List<UserResponse> userResponses = users.stream().map(UserMapper::toUserResponse).collect(Collectors.toList());

		return userResponses;
	}

	public UserResponse getUserById(Long userId) {
		User user = userRepository.findById(userId).get();

		UserResponse response = UserMapper.toUserResponse(user);
		return response;
	}

	public List<UserResponse> searchUserByKeyword(String keyword) {
		List<User> users = userRepository.findByUsernameContainingOrEmailContainingOrNameContaining(keyword, keyword,
				keyword);

		return users.stream().map(UserMapper::toUserResponse).collect(Collectors.toList());
	}

	public String updateUserById(Long userId, UserRequest userRequest) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		user.setUsername(userRequest.getUsername());
		user.setEmail(userRequest.getEmail());
		user.setName(userRequest.getName());

		userRepository.save(user);
		return "User updated successfully";
	}

	public String deleteUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		userRepository.delete(user);

		return "User deleted successfully";
	}

}
