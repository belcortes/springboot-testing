package com.example.usersapi.controllers;

import com.example.usersapi.models.User;
import com.example.usersapi.repositories.UserRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class UsersController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public Iterable<User> findAllUsers() {
		return userRepository.findAll();
	}

	@GetMapping("/{userId}")
	public User findUserById(@PathVariable Long userId) throws NotFoundException {

		User foundUser = userRepository.findOne(userId);

		if (foundUser == null) {
			throw new NotFoundException("User with ID of " + userId + " was not found!");
		}

		return foundUser;
	}

	@DeleteMapping("/{userId}")
	public HttpStatus deleteUserById(@PathVariable Long userId) throws EmptyResultDataAccessException {

		userRepository.delete(userId);
		return HttpStatus.OK;
	}

	// Exception handlers

	@ExceptionHandler
	void handleUserNotFound(
		NotFoundException exception,
		HttpServletResponse response) throws IOException {

		response.sendError(HttpStatus.NOT_FOUND.value(), exception.getMessage());
	}

	@ExceptionHandler
	void handleDeleteNotFoundException(
		EmptyResultDataAccessException exception,
		HttpServletResponse response) throws IOException {

		response.sendError(HttpStatus.NOT_FOUND.value());
	}

}