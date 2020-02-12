package ua.quiz.model.service.impl;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.quiz.model.dto.Role;
import ua.quiz.model.dto.User;
import ua.quiz.model.entity.RoleEntity;
import ua.quiz.model.entity.UserEntity;
import ua.quiz.model.exception.EmailAlreadyTakenException;
import ua.quiz.model.exception.EntityNotFoundException;
import ua.quiz.model.repository.UserRepository;
import ua.quiz.model.service.mapper.UserMapper;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UserServiceImpl.class)
public class UserServiceImplTest {
    private static final Long ID = 1L;

    private static final String EMAIL = "email@email.com";

    private static final String PASSWORD = "password";

    private static final String NAME = "Name";

    private static final String SURNAME = "Surname";

    private static final Long TEAM_ID = 2L;

    private static final User USER = User.builder()
            .id(ID)
            .name(NAME)
            .surname(SURNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .role(Role.PLAYER)
            .build();

    private static final UserEntity USER_ENTITY = getUserEntity();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @MockBean
    private UserRepository userDao;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserServiceImpl userService;

    @After
    public void resetMocks() {
        Mockito.reset(userDao, passwordEncoder, userMapper);
    }

    @Test
    public void registerShouldHaveNormalBehaviour() {
        when(userDao.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);


        userService.register(USER);

        verify(userDao).save(any());
    }

    @Test
    public void registerShouldThrowExceptionDueToNullUser() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Null user passed to register");

        userService.register(null);
    }

    @Test
    public void registerShouldThrowExceptionEmailTaken() {
        expectedException.expect(EmailAlreadyTakenException.class);
        expectedException.expectMessage("User with such email already exists");

        when(userDao.findByEmail(EMAIL)).thenReturn(Optional.of(USER_ENTITY));

        userService.register(USER);
    }

    @Test
    public void updateShouldThrowErrorDueToNullPassed() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User passed to update is null");

        userService.update(null);
    }

    @Test
    public void updateShouldHaveNormalBehaviour() {
        when(userMapper.mapUserToUserEntity(USER)).thenReturn(USER_ENTITY);

        userService.update(USER);
        verify(userDao).save(USER_ENTITY);
    }

    @Test
    public void findByEmailShouldThrowErrorDueToNullPassed() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Email passed is null");

        userService.findByEmail(null);
    }

    @Test
    public void findByEmailShouldHaveNormalBehaviour() {
        when(userDao.findByEmail(EMAIL)).thenReturn(Optional.of(USER_ENTITY));
        when(userMapper.mapUserEntityToUser(USER_ENTITY)).thenReturn(USER);

        final User actual = userService.findByEmail(EMAIL);

        assertEquals(USER, actual);
    }

    @Test
    public void findByEmailShouldThrowEntityNotFound() {
        expectedException.expect(EntityNotFoundException.class);
        when(userDao.findByEmail(EMAIL)).thenReturn(Optional.empty());

        userService.findByEmail(EMAIL);
    }

    @Test
    public void findByTeamIdShouldThrowErrorDueToNullPassed() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Team ID passed is null");

        userService.findByTeamId(null);
    }


    @Test
    public void findByTeamIdShouldHaveNormalBehaviour() {
        when(userDao.findAllByTeamId(TEAM_ID)).thenReturn(singletonList(USER_ENTITY));
        when(userMapper.mapUserEntityToUser(USER_ENTITY)).thenReturn(USER);

        final List<User> actual = userService.findByTeamId(TEAM_ID);

        assertThat(actual, CoreMatchers.hasItem(USER));
    }

    private static UserEntity getUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(ID);
        entity.setName(NAME);
        entity.setSurname(SURNAME);
        entity.setEmail(EMAIL);
        entity.setPassword(PASSWORD);
        entity.setRole(RoleEntity.PLAYER);

        return entity;
    }
}