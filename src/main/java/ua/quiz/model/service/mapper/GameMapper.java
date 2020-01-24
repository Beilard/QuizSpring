package ua.quiz.model.service.mapper;

import ua.quiz.model.dto.Game;
import ua.quiz.model.dto.Phase;
import ua.quiz.model.dto.Status;
import ua.quiz.model.entity.GameEntity;
import ua.quiz.model.entity.PhaseEntity;
import ua.quiz.model.entity.StatusEntity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GameMapper {
    private final PhaseMapper phaseMapper = new PhaseMapper();

    public Game mapGameEntityToGame(GameEntity gameEntity) {
        if (Objects.isNull(gameEntity)) {
            return null;
        }
        return Game.builder()
                .id(gameEntity.getId())
                .numberOfQuestions(gameEntity.getNumberOfQuestions())
                .timePerQuestion(gameEntity.getTimePerQuestion())
                .teamId(gameEntity.getTeam().getId())
                .currentPhase((gameEntity.getCurrentPhase()))
                .status(Status.valueOf(gameEntity.getStatusEntity().name()))
                .phases(gameEntity.getPhases() == null ? null :
                        mapPhaseEntitiesToPhase(gameEntity.getPhases()))
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
        entity.setPhases(mapPhaseToPhaseEntities(game.getPhases()));

        return entity;
    }

    private List<Phase> mapPhaseEntitiesToPhase(List<PhaseEntity> phaseEntities) {
        return phaseEntities.stream()
                .map(phaseMapper::mapPhaseEntityToPhase)
                .collect(Collectors.toList());
    }

    private List<PhaseEntity> mapPhaseToPhaseEntities(List<Phase> phases) {
        return phases.stream()
                .map(phaseMapper::mapPhaseToPhaseEntity)
                .collect(Collectors.toList());
    }
}
