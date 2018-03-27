package com.example.usersapi.repositories;

import com.example.usersapi.models.User;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@Before
	public void setUp() {
		User firstUser = new User(
			"user_name",
			"some first name",
			"some last name"
		);

		User secondUser = new User(
			"second_user",
			"some other first name",
			"some other last name"
		);

		entityManager.persist(firstUser);
		entityManager.persist(secondUser);
		entityManager.flush();
	}

	@Test
	public void findAll_returnsAllUsers() {
		Iterable<User> usersFromDb = userRepository.findAll();

		assertThat(Iterables.size(usersFromDb), is(3));
	}

}