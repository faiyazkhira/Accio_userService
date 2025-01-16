package com.accio.userService.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

	/**
	 * Username or Email: Accepts either a valid email address or a username. -
	 * Email: Standard email format. - Username: 3-20 alphanumeric characters, with
	 * optional dots or underscores.
	 */

	// USERNAME
	@Size(max = 50, message = "Username or email should not exceed 50 characters")
	@Pattern(regexp = "^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$|^[a-zA-Z0-9._-]{3,20}$", message = "Provide a valid email or username (username: 3-20 alphanumeric characters)")
	@NotNull(message = "Provide email or username")
	private String userNameOrEmail;

	/**
	 * Password: Must be between 8 and 20 characters and include: - At least one
	 * uppercase letter. - At least one lowercase letter. - At least one digit. - At
	 * least one special character.
	 */

	// PASSWORD
	@NotNull(message = "Provide correct password")
	@Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
	private String password;

}
