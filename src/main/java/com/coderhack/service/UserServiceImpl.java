package com.coderhack.service;

import com.coderhack.dto.RegisterUserRequest;
import com.coderhack.dto.UpdateScoreRequest;
import com.coderhack.entity.User;
import com.coderhack.exception.UserAlreadyExistsException;
import com.coderhack.exception.UserNotFoundException;
import com.coderhack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "score"));
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public User registerUser(RegisterUserRequest request) {
        if (userRepository.existsById(request.getUserId())) {
            throw new UserAlreadyExistsException("User already exists with ID: " + request.getUserId());
        }
        User user = User.builder()
                .userId(request.getUserId())
                .username(request.getUsername())
                .score(0)
                .badges(new LinkedHashSet<>())
                .build();
        return userRepository.save(user);
    }

    @Override
    public User updateScore(String userId, UpdateScoreRequest request) {
        User user = getUserById(userId);
        user.setScore(request.getScore());
        user.setBadges(calculateBadges(request.getScore()));
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    // Badges accumulate as score increases: each threshold adds a new badge
    private Set<String> calculateBadges(int score) {
        Set<String> badges = new LinkedHashSet<>();
        if (score >= 1)  badges.add("Code Ninja");
        if (score >= 30) badges.add("Code Champ");
        if (score >= 60) badges.add("Code Master");
        return badges;
    }
}
