package ua.quiz.model.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.quiz.model.dto.*;
import ua.quiz.model.entity.GameEntity;
import ua.quiz.model.entity.PhaseEntity;
import ua.quiz.model.exception.EntityNotFoundException;
import ua.quiz.model.repository.GameRepository;
import ua.quiz.model.repository.PhaseRepository;
import ua.quiz.model.repository.QuestionRepository;
import ua.quiz.model.service.GameService;
import ua.quiz.model.service.mapper.GameMapper;
import ua.quiz.model.service.mapper.PhaseMapper;
import ua.quiz.model.service.mapper.QuestionMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service("gameService")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log4j
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final PhaseRepository phaseRepository;

    private final GameMapper gameMapper;


    @Override
    public Game startGame(Team team, int numberOfQuestions, int timePerQuestion) {
        if (team == null || numberOfQuestions <= 0 || timePerQuestion <= 0) {
            log.warn("Either team is null or number of question/" +
                    "time per question are less than 0 to start the game");
            throw new IllegalArgumentException("Either team is null or number of question/time per question" +
                    " are less than 0 to start the game");
        }

        final Game game = gameBuilder(team, numberOfQuestions, timePerQuestion);
        final GameEntity entityWithId = gameRepository.save(gameMapper.mapGameToGameEntity(game));

        return gameMapper.mapGameEntityToGame(entityWithId);
    }

    @Override
    public void finishGame(Game game) {
        if (game == null) {
            log.warn("Null game passed to finish");
            throw new IllegalArgumentException("Null game passed to finish");
        }
        final Game finishedGame = changeStatusToPending(game);

        gameRepository.save(gameMapper.mapGameToGameEntity(finishedGame));
    }

    @Override
    public Game startReview(Game game) {
        if (game == null || game.getStatus() != Status.PENDING) {
            log.warn("Game passed to start review is invalid");
            throw new IllegalArgumentException("Game passed to start review is invalid");
        }
        final Game gameWithSetPhases = changePhaseToZero(game);

        gameRepository.save(gameMapper.mapGameToGameEntity(gameWithSetPhases));
        return gameWithSetPhases;
    }

    @Override
    public void finishReview(Game game) {
        if (game == null) {
            log.warn("Null game passed to  finish review");
            throw new IllegalArgumentException("Null game passed to finish review");
        }
        final Game reviewedGame = changeStatusToReviewed(game);

        gameRepository.save(gameMapper.mapGameToGameEntity(reviewedGame));
    }

    @Override
    public Game findById(Long id) {
        if (id == null) {
            log.warn("Null id passed to find a game");
            throw new IllegalArgumentException("Null id passed to find a game");
        }
        final Optional<GameEntity> foundGameEntity = gameRepository.findById(id);
        return gameMapper.mapGameEntityToGame(foundGameEntity.orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public Page<Game> findAll(Pageable pageable) {
        Page<GameEntity> entities = gameRepository.findAll(pageable);

        return entities.isEmpty() ? null :
                entities.map(gameMapper::mapGameEntityToGame);
    }

    @Override
    public Long getCorrectAnswersCount(Game game) {
        if (game == null) {
            log.warn("Null id passed to get correct answers from the game");
            throw new IllegalArgumentException("Null id passed to get correct answers from the game");
        }

        List<PhaseEntity> phaseEntitiesByGameId = phaseRepository.findPhaseEntitiesByGameId(game.getId());
        return phaseEntitiesByGameId
                .stream()
                .filter(PhaseEntity::getIsCorrect)
                .count();
    }

    @Override
    public void updateGame(Game game) {
        if (game == null) {
            log.warn("Null game passed to update a game");
            throw new IllegalArgumentException("Null game passed to update a game");
        }
        gameRepository.save(gameMapper.mapGameToGameEntity(game));
    }

    @Override
    public Page<Game> findAllByTeamId(Long teamId, Pageable pageable) {
        if (teamId == null) {
            log.warn("Null id passed to find games by team id");
            throw new IllegalArgumentException("Null id passed to find games by team id");
        }
        Page<GameEntity> gamesOfTeam = gameRepository.findByTeamId(teamId, pageable);

        return gamesOfTeam.isEmpty() ? null :
                gamesOfTeam.map(gameMapper::mapGameEntityToGame);
    }

    private Game gameBuilder(Team team, int numberOfQuestions, int timePerQuestion) {
        return Game.builder()
                .numberOfQuestions(numberOfQuestions)
                .timePerQuestion(timePerQuestion)
                .team(team)
                .currentPhase(0)
                .status(Status.ONGOING)
                .build();
    }

    private Game changeStatusToPending(Game game) {
        return game.toBuilder()
                .status(Status.PENDING)
                .build();
    }

    private Game changeStatusToReviewed(Game game) {
        return game.toBuilder()
                .status(Status.REVIEWED)
                .build();
    }

    private Game changePhaseToZero(Game game) {
        return game.toBuilder()
                .currentPhase(0)
                .build();
    }
}
