package ua.quiz.model.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.quiz.model.dto.Team;
import ua.quiz.model.dto.User;
import ua.quiz.model.entity.TeamEntity;
import ua.quiz.model.entity.UserEntity;
import ua.quiz.model.exception.EntityAlreadyExistsException;
import ua.quiz.model.exception.EntityNotFoundException;
import ua.quiz.model.repository.TeamRepository;
import ua.quiz.model.repository.UserRepository;
import ua.quiz.model.service.TeamService;
import ua.quiz.model.service.mapper.TeamMapper;
import ua.quiz.model.service.mapper.UserMapper;

import java.util.Objects;
import java.util.Optional;

@Service("teamService")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log4j
public class TeamServiceImpl implements TeamService {

    private static final int MAX_TEAM_NAME_LENGTH = 31;
    private static final int MIN_TEAM_NAME_LENGTH = 1;

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final UserMapper userMapper;


    @Override
    public void createTeam(String teamName) {
        if (teamName.length() < MIN_TEAM_NAME_LENGTH || teamName.length() > MAX_TEAM_NAME_LENGTH) {
            log.warn("Team name length is out of bounds");
            throw new IllegalArgumentException("Team name length is out of bounds");
        }
        Optional<TeamEntity> teamFoundByName = teamRepository.findByTeamName(teamName);

        if (teamFoundByName.isPresent()) {
            log.info("User tried to create a team with reserved name: " + teamName);
            throw new EntityAlreadyExistsException("User tried to create a team with reserved name: " + teamName);
        }
        final TeamEntity team = new TeamEntity();
        team.setTeamName(teamName);
        teamRepository.save(team);
    }

    @Override
    public void changeCaptain(User newCaptain, User oldCaptain) {
        if (newCaptain == null || oldCaptain == null
                || !Objects.equals(newCaptain.getTeam().getId(), oldCaptain.getTeam().getId())
                || newCaptain.getIsCaptain()) {
            log.warn("New captain or old captain passed were null or illegal");
            throw new IllegalArgumentException("New captain or old captain passed were null or illegal");
        }

        User oldCaptainWithEmail = getOldCaptainWithEmail(oldCaptain);
        userRepository.save(userMapper.mapUserToUserEntity(changeCaptainStatus(oldCaptainWithEmail, false)));
        userRepository.save(userMapper.mapUserToUserEntity(changeCaptainStatus(newCaptain, true)));
    }

    @Override
    public void joinTeam(User user, Long teamId) {
        if (user == null || teamId == null || teamId <= 0) {
            log.warn("User or teamId passed to join team are null or illegal");
            throw new IllegalArgumentException("User or teamId passed to join team are null or illegal");
        }
        final UserEntity userEntity = userRepository.findById(user.getId()).orElseThrow(EntityNotFoundException::new);
        userEntity.setTeam(teamRepository.findById(teamId).orElseThrow(EntityNotFoundException::new));

        userRepository.save(userEntity);
    }

    @Override
    public void leaveTeam(User user) {
        if (user == null || user.getIsCaptain()) {
            log.warn("User passed to leave is null or captain");
            throw new IllegalArgumentException("User passed to leave is null or captain");
        }

        userRepository.save(userMapper.mapUserToUserEntity(removeTeam(user)));
    }

    @Override
    public Team findTeamByName(String name) {
        if (name == null) {
            log.warn("String provided for findTeamByName was null");
            throw new IllegalArgumentException("String provided for findTeamByName was null");
        }
        final TeamEntity teamEntity = teamRepository.findByTeamName(name).orElseThrow(EntityNotFoundException::new);

        return teamMapper.mapTeamEntityToTeam(teamEntity);
    }

    private User changeCaptainStatus(User user, boolean isCaptain) {
        if (user == null) {
            log.warn("User passed to change captaincy is null");
            throw new IllegalArgumentException("User passed to change captaincy is null");
        }

        return user.toBuilder()
                .isCaptain(isCaptain)
                .build();
    }

    private User removeTeam(User user) {
        return user.toBuilder()
                .team(null)
                .build();
    }

    private User getOldCaptainWithEmail(User oldCaptain) {
        return userMapper.mapUserEntityToUser(userRepository.findByEmail(oldCaptain.getEmail())
                .orElseThrow(EntityNotFoundException::new));
    }
}
