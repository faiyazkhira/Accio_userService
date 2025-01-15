package com.accio.userService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accio.userService.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String username);

	List<User> findByUsernameContainingOrEmailContainingOrNameContaining(String usernameKeyword, String emailKeyword,
			String nameKeyword);

}
