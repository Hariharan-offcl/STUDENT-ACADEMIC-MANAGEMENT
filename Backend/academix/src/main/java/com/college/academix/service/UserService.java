package com.college.academix.service;

import com.college.academix.model.User;
import com.college.academix.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    public User createUser(User user){
        return userRepository.save(user);
    }
}