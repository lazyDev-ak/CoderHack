package com.coderhack.service;

import com.coderhack.dto.RegisterUserRequest;
import com.coderhack.dto.UpdateScoreRequest;
import com.coderhack.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(String userId);

    User registerUser(RegisterUserRequest request);

    User updateScore(String userId, UpdateScoreRequest request);

    void deleteUser(String userId);
}
