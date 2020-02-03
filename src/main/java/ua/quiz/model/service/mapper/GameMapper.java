package ua.quiz.model.service.mapper;

import org.springframework.stereotype.Component;
import ua.quiz.model.dto.Game;
import ua.quiz.model.dto.Status;
import ua.quiz.model.entity.GameEntity;
import ua.quiz.model.entity.StatusEntity;

import java.util.Objects;

@Component
public class GameMapper {
    private final TeamMapper teamMapper = new TeamMapper();

    public Game mapGameEntityToGame(GameEntity gameEntity) {
        if (Objects.isNull(gameEntity)) {
            return null;
        }
        return Game.builder()
                .id(gameEntity.getId())
                .numberOfQuestions(gameEntity.getNumberOfQuestions())
                .timePerQuestion(gameEntity.getTimePerQuestion())
                .team(teamMapper.mapTeamEntityToTeam(gameEntity.getTeam()))
                .currentPhase((gameEntity.getCurrentPhase()))
                .status(Status.valueOf(gameEntity.getStatusEntity().name()))
                .build();
    }

    public GameEntity mapGameToGameEntity(Game game) {
        if (Objects.isNull(game)) {
            return null;
        }

        GameEntity entity = new GameEntity();

        entity.setId(game.getId());
        entity.setNumberOfQuestions(game.getNumberOfQuestions());
        entity.setTimePerQuestion(game.getTimePerQuestion());
        entity.setCurrentPhase(game.getCurrentPhase());
        entity.setStatusEntity(StatusEntity.valueOf(game.getStatus().name()));
        entity.setTeam(teamMapper.mapTeamToTeamEntity(game.getTeam()));

        return entity;
    }
}
