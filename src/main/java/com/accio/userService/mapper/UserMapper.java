package com.accio.userService.mapper;

import com.accio.userService.dto.UserResponse;
import com.accio.userService.entity.User;

public class UserMapper {

	// Method to convert User entity to UserResponse DTO
	public static UserResponse toUserResponse(User user) {
		if (user == null) {
			return null; // Handle null case, could return a default or throw an exception based on your
							// needs
		}
		return new UserResponse(user.getId().toString(), // Convert Long to String for the id
				user.getUsername(), user.getName(), user.getEmail());
	}

	// Method to convert UserResponse DTO to User entity
	public static User toUser(UserResponse userResponse) {
		if (userResponse == null) {
			return null; // Handle null case
		}
		User user = new User();
		user.setId(Long.parseLong(userResponse.getId())); // Assuming the ID in UserResponse is a String and needs to be
															// parsed to Long
		user.setUsername(userResponse.getUsername());
		user.setName(userResponse.getName());
		user.setEmail(userResponse.getEmail());
		return user;
	}
}
