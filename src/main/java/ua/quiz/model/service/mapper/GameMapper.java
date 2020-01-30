package ua.quiz.model.service.mapper;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.quiz.model.dto.Game;
import ua.quiz.model.dto.Phase;
import ua.quiz.model.dto.Status;
import ua.quiz.model.entity.GameEntity;
import ua.quiz.model.entity.PhaseEntity;
import ua.quiz.model.entity.StatusEntity;
import ua.quiz.model.repository.TeamRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GameMapper {
    private PhaseMapper phaseMapper;
    private TeamRepository teamRepository;

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
        entity.setTeam(teamRepository.findById(game.getTeamId()).orElseThrow(IllegalArgumentException::new));

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
