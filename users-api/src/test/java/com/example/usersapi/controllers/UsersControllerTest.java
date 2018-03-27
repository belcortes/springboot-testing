package com.example.usersapi.controllers;

import com.example.usersapi.models.User;
import com.example.usersapi.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UsersController.class)
public class UsersControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserRepository mockUserRepository;

	@Before
	public void setUp() {
		User firstUser = new User(
			"someone",
			"Ima",
			"Person"
		);

		User secondUser = new User(
			"someone_else",
			"Someone",
			"Else"
		);

		Iterable<User> mockUsers =
			Stream.of(firstUser, secondUser).collect(Collectors.toList());

		given(mockUserRepository.findAll()).willReturn(mockUsers);
		given(mockUserRepository.findOne(1L)).willReturn(firstUser);
		given(mockUserRepository.findOne(4L)).willReturn(null);

		// Mock out Delete to return EmptyResultDataAccessException for missing user with ID of 4
		doAnswer(invocation -> {
			throw new EmptyResultDataAccessException("ERROR MESSAGE FROM MOCK!!!", 1234);
		}).when(mockUserRepository).delete(4L);
	}

	@Test
	public void findAllUsers_success_returnsStatusOK() throws Exception {

		this.mockMvc
			.perform(get("/"))
			.andExpect(status().isOk());
	}

	@Test
	public void findAllUsers_success_returnAllUsersAsJSON() throws Exception {

		this.mockMvc
			.perform(get("/"))
			.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void findAllUsers_success_returnUserNameForEachUser() throws Exception {

		this.mockMvc
			.perform(get("/"))
			.andExpect(jsonPath("$[0].userName", is("someone")));
	}

	@Test
	public void findAllUsers_success_returnFirstNameForEachUser() throws Exception {

		this.mockMvc
			.perform(get("/"))
			.andExpect(jsonPath("$[0].firstName", is("Ima")));
	}

	@Test
	public void findAllUsers_success_returnLastNameForEachUser() throws Exception {

		this.mockMvc
			.perform(get("/"))
			.andExpect(jsonPath("$[0].lastName", is("Person")));
	}

	@Test
	public void findUserById_success_returnsStatusOK() throws Exception {

		this.mockMvc
			.perform(get("/1"))
			.andExpect(status().isOk());
	}

	@Test
	public void findUserById_success_returnUserName() throws Exception {

		this.mockMvc
			.perform(get("/1"))
			.andExpect(jsonPath("$.userName", is("someone")));
	}

	@Test
	public void findUserById_success_returnFirstName() throws Exception {

		this.mockMvc
			.perform(get("/1"))
			.andExpect(jsonPath("$.firstName", is("Ima")));
	}

	@Test
	public void findUserById_success_returnLastName() throws Exception {

		this.mockMvc
			.perform(get("/1"))
			.andExpect(jsonPath("$.lastName", is("Person")));
	}

	@Test
	public void findUserById_failure_userNotFoundReturns404() throws Exception {

		this.mockMvc
			.perform(get("/4"))
			.andExpect(status().isNotFound());
	}

	@Test
	public void findUserById_failure_userNotFoundReturnsNotFoundErrorMessage() throws Exception {

		this.mockMvc
			.perform(get("/4"))
			.andExpect(status().reason(containsString("User with ID of 4 was not found!")));
	}

	@Test
	public void deleteUserById_success_returnsStatusOk() throws Exception {

		this.mockMvc
			.perform(delete("/1"))
			.andExpect(status().isOk());
	}

	@Test
	public void deleteUserById_success_deletesViaRepository() throws Exception {

		this.mockMvc.perform(delete("/1"));

		verify(mockUserRepository, times(1)).delete(1L);
	}

	@Test
	public void deleteUserById_failure_userNotFoundReturns404() throws Exception {

		this.mockMvc
			.perform(delete("/4"))
			.andExpect(status().isNotFound());
	}
}