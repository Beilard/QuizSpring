package ua.quiz.model.service.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.quiz.model.dto.Role;
import ua.quiz.model.dto.Team;
import ua.quiz.model.dto.User;
import ua.quiz.model.entity.RoleEntity;
import ua.quiz.model.entity.TeamEntity;
import ua.quiz.model.entity.UserEntity;
import ua.quiz.model.service.impl.GameServiceImpl;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UserMapper.class)
public class UserMapperTest {
    private static final Long ID = 0L;

    private static final String NAME = "Ivan";

    private static final String SURNAME = "Popov";

    private static final String EMAIL = "Ivan@mail.com";

    private static final String PASSWORD = "Qwerty123#";

    private static final Team TEAM = new Team(1L, "Name");

    private static final TeamEntity TEAM_ENTITY = getTeamEntity();

    @Autowired
    private UserMapper userMapper;
    @MockBean
    private TeamMapper teamMapper;

    @Test
    public void mapEntityToUserShouldReturnUser() {
        Mockito.when(teamMapper.mapTeamEntityToTeam(TEAM_ENTITY)).thenReturn(TEAM);

        final UserEntity userEntity = new UserEntity();
        userEntity.setId(ID);
        userEntity.setTeam(TEAM_ENTITY);
        userEntity.setEmail(EMAIL);
        userEntity.setIsCaptain(true);
        userEntity.setSurname(SURNAME);
        userEntity.setName(NAME);
        userEntity.setRole(RoleEntity.PLAYER);
        userEntity.setPassword(PASSWORD);

        final User user = userMapper.mapUserEntityToUser(userEntity);
        assertThat("mapping id has failed", user.getId(), is(ID));
        assertThat("mapping name has failed", user.getName(), is(NAME));
        assertThat("mapping surname has failed", user.getSurname(), is(SURNAME));
        assertThat("mapping email has failed", user.getEmail(), is(EMAIL));
        assertThat("mapping password has failed", user.getPassword(), is(PASSWORD));
        assertThat("mapping team has failed", user.getTeam(), is(TEAM));
        assertThat("mapping roles has failed", user.getRole(), is(Role.PLAYER));
    }

    @Test
    public void mapUserToUserEntityShouldReturnUserEntity() {
        Mockito.when(teamMapper.mapTeamToTeamEntity(TEAM)).thenReturn(TEAM_ENTITY);

        final User user = User.builder()
                .id(ID)
                .email(EMAIL)
                .password(PASSWORD)
                .name(NAME)
                .surname(SURNAME)
                .role(Role.PLAYER)
                .team(TEAM)
                .build();

        final UserEntity userEntity = userMapper.mapUserToUserEntity(user);
        assertThat("mapping id has failed", userEntity.getId(), is(ID));
        assertThat("mapping name has failed", userEntity.getName(), is(NAME));
        assertThat("mapping surname has failed", userEntity.getSurname(), is(SURNAME));
        assertThat("mapping email has failed", userEntity.getEmail(), is(EMAIL));
        assertThat("mapping password has failed", userEntity.getPassword(), is(PASSWORD));
        assertThat("mapping teamId has failed", userEntity.getTeam(), is(TEAM_ENTITY));
        assertThat("mapping roles has failed", userEntity.getRole(), is(RoleEntity.PLAYER));
    }

    private static TeamEntity getTeamEntity() {
        TeamEntity teamEntity = new TeamEntity();

        teamEntity.setTeamName("Name");
        teamEntity.setId(1L);

        return teamEntity;
    }

}