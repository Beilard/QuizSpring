package ua.quiz.model.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.quiz.model.dto.User;
import ua.quiz.model.entity.UserEntity;
import ua.quiz.model.exception.EmailAlreadyTakenException;
import ua.quiz.model.exception.EntityNotFoundException;
import ua.quiz.model.exception.InvalidCredentialsException;
import ua.quiz.model.repository.UserRepository;
import ua.quiz.model.service.UserService;
import ua.quiz.model.service.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("userService")
@AllArgsConstructor
@Log4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserMapper userMapper;

    @Override
    public void register(User user) {
        if (user == null) {
            log.warn("Null user passed to register");
            throw new IllegalArgumentException("Null user passed to register");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("User with such email already exists");
            throw new EmailAlreadyTakenException("User with such email already exists");
        }

        final String encoded = encoder.encode(user.getPassword());
        user.setPassword(encoded);

        userRepository.save(userMapper.mapUserToUserEntity(user));
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null) {
            log.warn("Null email passed");
            throw new InvalidCredentialsException("Incorrect email password provided");
        }

        Optional<UserEntity> entity = userRepository.findByEmail(email);

        if (!entity.isPresent()) {
            log.warn("User entity wasn't found");
            throw new EntityNotFoundException("User entity wasn't found");
        }

        return userMapper.mapUserEntityToUser(entity.get());
    }

    @Override
    public List<User> findByTeamId(Long teamId) {
        if (teamId == null) {
            log.warn("team ID passed is null");
            throw new IllegalArgumentException("team ID passed is null");
        }

        final List<UserEntity> entities = userRepository.findAllByTeamId(teamId);

        return mapUserEntityListToUserList(entities);
    }

    @Override
    public User findByEmail(String email) {
        if (email == null) {
            log.warn("email passed is null");
            throw new InvalidCredentialsException("email passed is null");
        }

        return userRepository.findByEmail(email)
                .map(userMapper::mapUserEntityToUser)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public void update(User user) {
        if (user == null) {
            log.warn("User passed to update is null");
            throw new InvalidCredentialsException("User passed to update is null");
        }

        userRepository.save(userMapper.mapUserToUserEntity(user));
    }

    private List<User> mapUserEntityListToUserList(List<UserEntity> entities) {
        return entities.stream()
                .map(userMapper::mapUserEntityToUser)
                .collect(Collectors.toList());
    }
}
