package ua.quiz.model.service.mapper;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.quiz.model.dto.Role;
import ua.quiz.model.dto.User;
import ua.quiz.model.entity.RoleEntity;
import ua.quiz.model.entity.UserEntity;

import java.util.Objects;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserMapper {
    private TeamMapper teamMapper;

    public User mapUserEntityToUser(UserEntity userEntity) {
        if (Objects.isNull(userEntity)) {
            return null;
        }
        return User.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .name(userEntity.getName())
                .surname(userEntity.getSurname())
                .isCaptain(userEntity.getIsCaptain())
                .team(teamMapper.mapTeamEntityToTeam(userEntity.getTeam()))
                .role(Role.valueOf((userEntity.getRole().name())))
                .build();
    }

    public UserEntity mapUserToUserEntity(User user) {
        if (Objects.isNull(user)) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setName(user.getName());
        entity.setSurname(user.getSurname());
        entity.setIsCaptain(user.getIsCaptain() == null ? false : user.getIsCaptain());
        entity.setTeam(teamMapper.mapTeamToTeamEntity(user.getTeam()));
        entity.setRole(getRoleForEntity(user));

        return entity;
    }

    private RoleEntity getRoleForEntity(User user) {
        if (user.getRole() == null) {
            return RoleEntity.PLAYER;
        }
        return RoleEntity.valueOf(user.getRole().name());
    }
}
