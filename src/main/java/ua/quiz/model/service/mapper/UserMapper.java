package ua.quiz.model.service.mapper;

import ua.quiz.model.dto.Role;
import ua.quiz.model.dto.User;
import ua.quiz.model.entity.RoleEntity;
import ua.quiz.model.entity.UserEntity;

import java.util.Objects;

public class UserMapper {
    private final TeamMapper teamMapper = new TeamMapper();

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
        entity.setIsCaptain(user.getIsCaptain());
        entity.setTeam(teamMapper.mapTeamToTeamEntity(user.getTeam()));
        entity.setRole(RoleEntity.valueOf(user.getRole().name()));

        return entity;
    }
}