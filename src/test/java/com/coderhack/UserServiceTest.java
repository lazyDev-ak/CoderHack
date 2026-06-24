package com.coderhack;

import com.coderhack.dto.RegisterUserRequest;
import com.coderhack.dto.UpdateScoreRequest;
import com.coderhack.entity.User;
import com.coderhack.exception.UserAlreadyExistsException;
import com.coderhack.exception.UserNotFoundException;
import com.coderhack.repository.UserRepository;
import com.coderhack.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId("u1")
                .username("alice")
                .score(0)
                .badges(new LinkedHashSet<>())
                .build();
    }

    @Test
    void registerUser_success() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUserId("u1");
        request.setUsername("alice");

        when(userRepository.existsById("u1")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(request);

        assertThat(result.getUserId()).isEqualTo("u1");
        assertThat(result.getScore()).isEqualTo(0);
        assertThat(result.getBadges()).isEmpty();
    }

    @Test
    void registerUser_duplicateId_throwsException() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUserId("u1");
        request.setUsername("alice");

        when(userRepository.existsById("u1")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void getUserById_found() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        User result = userService.getUserById("u1");

        assertThat(result.getUserId()).isEqualTo("u1");
    }

    @Test
    void getUserById_notFound_throwsException() {
        when(userRepository.findById("u99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById("u99"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getAllUsers_returnsSortedByScore() {
        User userA = User.builder().userId("u1").username("alice").score(80).badges(new LinkedHashSet<>()).build();
        User userB = User.builder().userId("u2").username("bob").score(40).badges(new LinkedHashSet<>()).build();

        when(userRepository.findAll(Sort.by(Sort.Direction.DESC, "score")))
                .thenReturn(List.of(userA, userB));

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getScore()).isGreaterThan(result.get(1).getScore());
    }

    @Test
    void updateScore_assignsBadgesCorrectly_scoreBelow30() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateScoreRequest request = new UpdateScoreRequest();
        request.setScore(15);

        User result = userService.updateScore("u1", request);

        assertThat(result.getScore()).isEqualTo(15);
        assertThat(result.getBadges()).containsExactly("Code Ninja");
    }

    @Test
    void updateScore_assignsBadgesCorrectly_score30to59() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateScoreRequest request = new UpdateScoreRequest();
        request.setScore(45);

        User result = userService.updateScore("u1", request);

        assertThat(result.getBadges()).containsExactly("Code Ninja", "Code Champ");
    }

    @Test
    void updateScore_assignsBadgesCorrectly_score60to100() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateScoreRequest request = new UpdateScoreRequest();
        request.setScore(90);

        User result = userService.updateScore("u1", request);

        assertThat(result.getBadges()).containsExactly("Code Ninja", "Code Champ", "Code Master");
    }

    @Test
    void updateScore_score0_noBadges() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateScoreRequest request = new UpdateScoreRequest();
        request.setScore(0);

        User result = userService.updateScore("u1", request);

        assertThat(result.getBadges()).isEmpty();
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById("u1")).thenReturn(true);
        doNothing().when(userRepository).deleteById("u1");

        assertThatCode(() -> userService.deleteUser("u1")).doesNotThrowAnyException();
        verify(userRepository).deleteById("u1");
    }

    @Test
    void deleteUser_notFound_throwsException() {
        when(userRepository.existsById("u99")).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser("u99"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void badges_areUnique_noduplicates() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateScoreRequest request = new UpdateScoreRequest();
        request.setScore(100);

        User result = userService.updateScore("u1", request);

        Set<String> badges = result.getBadges();
        assertThat(badges).hasSize(3);
        assertThat(badges).doesNotHaveDuplicates();
    }
}
