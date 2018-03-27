package com.example.springbootmicroservicewrapperwithtests.repositories;

import com.example.springbootmicroservicewrapperwithtests.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}