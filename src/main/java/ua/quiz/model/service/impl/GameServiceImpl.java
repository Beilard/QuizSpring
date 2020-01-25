package ua.quiz.model.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.quiz.model.dto.Game;
import ua.quiz.model.dto.Phase;
import ua.quiz.model.dto.Question;
import ua.quiz.model.dto.Status;
import ua.quiz.model.entity.GameEntity;
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
    private final QuestionRepository questionRepository;
    private final GameMapper gameMapper;
    private final PhaseMapper phaseMapper;
    private final QuestionMapper questionMapper;

    @Override
    public Game startGame(Long teamId, int numberOfQuestions, int timePerQuestion) {
        if (teamId == null || numberOfQuestions <= 0 || timePerQuestion <= 0) {
            log.warn("Either teamId is null or number of question/" +
                    "time per question are less than 0 to start the game");
            throw new IllegalArgumentException("Either teamId is null or number of question/time per question" +
                    " are less than 0 to start the game");
        }
        final Game game = gameBuilder(teamId, numberOfQuestions, timePerQuestion);
        final Long gameId = gameRepository.save(gameMapper.mapGameToGameEntity(game)).getId();

        return createGameWithPhases(numberOfQuestions, game, gameId);
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
    public List<Game> findAll(Integer page, Integer rowCount) {
        final PageRequest pageRequest = PageRequest.of(page, rowCount);
        Page<GameEntity> entities = gameRepository.findAll(pageRequest);

        return entities.isEmpty() ? Collections.emptyList() :
                mapGameEntityListToGameList(entities);
    }

    @Override
    public Long countAllEntries() {
        return gameRepository.count();
    }

    @Override
    public Long countAllByTeamId(Long teamId) {
        if (teamId == null) {
            log.warn("Null teamId passed to count games");
            throw new IllegalArgumentException("Null teamId passed to count games");
        }
        return gameRepository.countAllByTeamId(teamId);
    }

    @Override
    public Long getCorrectAnswersCount(Game game) {
        if (game == null) {
            log.warn("Null id passed to get correct answers from the game");
            throw new IllegalArgumentException("Null id passed to get correct answers from the game");
        }
        return game.getPhases()
                .stream()
                .filter(Phase::getIsCorrect)
                .count();
    }

    @Override
    public void updateGame(Game game) {
        if (game == null) {
            log.warn("Null id passed to update a game");
            throw new IllegalArgumentException("Null id passed to update a game");
        }
        gameRepository.save(gameMapper.mapGameToGameEntity(game));
    }

    @Override
    public List<Game> findAllByTeamId(Long teamId, Integer page, Integer rowCount) {
        if (teamId == null) {
            log.warn("Null id passed to find games by team id");
            throw new IllegalArgumentException("Null id passed to find games by team id");
        }
        final PageRequest pageRequest = PageRequest.of(page, rowCount);
        Page<GameEntity> gamesOfTeam = gameRepository.findByTeamId(teamId, pageRequest);

        return gamesOfTeam.isEmpty() ? Collections.emptyList() :
                mapGameEntityListToGameList(gamesOfTeam);
    }

    private Game createGameWithPhases(int numberOfQuestions, Game game, Long gameId) {
        return game.toBuilder()
                .id(gameId)
                .currentPhase(0)
                .phases(returnPhaseList(gameId, numberOfQuestions))
                .build();
    }

    private List<Phase> returnPhaseList(Long gameId, Integer numberOfPhases) {
        Long amountOfQuestionsInDb = questionRepository.count();
        if (numberOfPhases > amountOfQuestionsInDb) {
            log.warn("Amount of questions requested is bigger than the count in DB");
            throw new IllegalArgumentException("Amount of questions requested is bigger than the count in DB");
        }
        saveGeneratedPhases(gameId, numberOfPhases, amountOfQuestionsInDb);

        return phaseRepository.findPhaseEntitiesByGameId(gameId)
                .stream()
                .map(phaseMapper::mapPhaseEntityToPhase)
                .collect(Collectors.toList());
    }

    private void saveGeneratedPhases(Long gameId, Integer numberOfPhases, Long amountOfQuestionsInDb) {
        List<Long> generatedIds = generateIds(numberOfPhases, amountOfQuestionsInDb);

        for (int i = 0; i < numberOfPhases; i++) {
            final Phase phase = generatePhase(gameId, generatedIds, i);

            phaseRepository.save(phaseMapper.mapPhaseToPhaseEntity(phase));
        }
    }

    private Phase generatePhase(Long gameId, List<Long> generatedIds, int i) {
        final Question question = questionRepository.findById(generatedIds.get(i))
                .map(questionMapper::mapQuestionEntityToQuestion)
                .orElseThrow(EntityNotFoundException::new);

        return Phase.builder()
                .gameId(gameId)
                .startTime(LocalDateTime.now())
                .deadline(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .question(question)
                .build();
    }

    private List<Long> generateIds(Integer numberOfPhases, Long amountOfQuestionsInDb) {
        final Random random = new Random();
        return random
                .longs(1, amountOfQuestionsInDb)
                .distinct()
                .limit(numberOfPhases)
                .boxed()
                .collect(Collectors.toList());
    }

    private Game gameBuilder(Long teamId, int numberOfQuestions, int timePerQuestion) {
        return Game.builder()
                .numberOfQuestions(numberOfQuestions)
                .timePerQuestion(timePerQuestion)
                .teamId(teamId)
                .phases(Collections.emptyList())
                .status(Status.ONGOING)
                .build();
    }

    private List<Game> mapGameEntityListToGameList(Page<GameEntity> gameEntities) {
        return gameEntities.stream()
                .map(gameMapper::mapGameEntityToGame)
                .collect(Collectors.toList());
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
