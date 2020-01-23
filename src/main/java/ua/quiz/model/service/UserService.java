package ua.quiz.model.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ua.quiz.model.dto.User;

import java.util.List;

public interface UserService extends UserDetailsService {
    void register(User user);

    User login(String email, String password);

    List<User> findByTeamId(Long teamId);

    User findByEmail(String email);

    void update(User user);
}
