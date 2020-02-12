package ua.quiz.model.service.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.quiz.model.dto.*;
import ua.quiz.model.entity.*;
import ua.quiz.model.repository.GameRepository;
import ua.quiz.model.repository.PhaseRepository;
import ua.quiz.model.repository.QuestionRepository;
import ua.quiz.model.service.mapper.GameMapper;
import ua.quiz.model.service.mapper.PhaseMapper;
import ua.quiz.model.service.mapper.QuestionMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = GameServiceImpl.class)
public class GameServiceImplTest {
    private static final Long QUESTION_ID = 1L;

    private static final Long PHASE_ID = 2L;

    private static final Long TEAM_ID = 3L;

    private static final Long GAME_ID = 4L;

    private static final int NUMBER_OF_QUESTIONS = 1;

    private static final int TIME_PER_QUESTION = 60;

    private static final long QUESTION_ENTRIES = 1L;

    private static final int CURRENT_PHASE = 0;

    private static final long GAME_ENTRIES = 1L;

    private static final int AMOUNT_OF_CORRECT_ANSWERS = 0;

    private static final String HINT = "Hint";

    private static final String QUESTION_BODY = "Question body";

    private static final String CORRECT_ANSWER = "Correct";

    private static final String TEAM_NAME = "name";

    private static final Team TEAM = new Team(TEAM_ID, TEAM_NAME);

    private static final TeamEntity TEAM_ENTITY = getTeamEntity();

    private static final Question QUESTION = Question.builder()
            .id(QUESTION_ID)
            .body(QUESTION_BODY)
            .correctAnswer(CORRECT_ANSWER)
            .hint(HINT)
            .build();

    private static final Phase PHASE = Phase.builder()
            .id(PHASE_ID)
            .question(QUESTION)
            .isCorrect(true)
            .build();

    private static final Game GAME = Game.builder()
            .numberOfQuestions(NUMBER_OF_QUESTIONS)
            .timePerQuestion(TIME_PER_QUESTION)
            .team(TEAM)
            .status(Status.ONGOING)
            .build();

    private static final GameEntity GAME_ENTITY = getGameEntity();


    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private GameServiceImpl gameService;
    @MockBean
    private PhaseRepository phaseDao;
    @MockBean
    private QuestionRepository questionRepository;
    @MockBean
    private GameRepository gameDao;
    @MockBean
    private PhaseMapper phaseMapper;
    @MockBean
    private QuestionMapper questionMapper;
    @MockBean
    private GameMapper gameMapper;

    @Test
    public void startGameShouldThrowExceptionDueToNullTeamId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Either team is null or number of question/time per question" +
                " are less than 0 to start the game");

        gameService.startGame(null, NUMBER_OF_QUESTIONS, TIME_PER_QUESTION);
    }

    @Test
    public void startGameShouldThrowExceptionDueToWrongNumberOfQuestions() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Either team is null or number of question/time per question" +
                " are less than 0 to start the game");

        gameService.startGame(TEAM, 0, TIME_PER_QUESTION);
    }

    @Test
    public void startGameShouldThrowExceptionDueToWrongTimePerQuestion() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Either team is null or number of question/time per question" +
                " are less than 0 to start the game");

        gameService.startGame(TEAM, NUMBER_OF_QUESTIONS, 0);
    }

    @Test
    public void startGameShouldHaveNormalBehaviour() {
        when(gameMapper.mapGameToGameEntity(GAME)).thenReturn(GAME_ENTITY);
        when(gameDao.save(any())).thenReturn(GAME_ENTITY);
        when(gameMapper.mapGameEntityToGame(GAME_ENTITY)).thenReturn(GAME.toBuilder()
                .id(GAME_ID)
                .currentPhase(CURRENT_PHASE)
                .build());

        final Game actual = gameService.startGame(TEAM, NUMBER_OF_QUESTIONS, TIME_PER_QUESTION);
        final Game expected = GAME.toBuilder()
                .id(GAME_ID)
                .currentPhase(CURRENT_PHASE)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void finishGameShouldThrowExceptionDueToNullGame() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Null game passed to finish");

        gameService.finishGame(null);
    }

    @Test
    public void finishGameShouldHaveNormalBehaviour() {
        final Game pendingGame = GAME.toBuilder()
                .status(Status.PENDING)
                .build();

        when(gameMapper.mapGameToGameEntity(pendingGame)).thenReturn(GAME_ENTITY);

        gameService.finishGame(GAME);
        verify(gameDao).save(GAME_ENTITY);

    }

    @Test
    public void findByIdShouldThrowExceptionDueToNullId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Null id passed to find a game");

        gameService.findById(null);
    }

    @Test
    public void findByIdShouldHaveNormalBehaviour() {
        when(gameMapper.mapGameEntityToGame(GAME_ENTITY)).thenReturn(GAME);
        when(gameDao.findById(GAME_ID)).thenReturn(Optional.of(GAME_ENTITY));

        Game actual = gameService.findById(GAME_ID);

        assertEquals(GAME, actual);
    }

    @Test
    public void getCorrectAnswersCountShouldThrowExceptionDueToNullGame() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Null game passed to get correct amount of answers");

        gameService.getCorrectAnswersCount(null);
    }

    @Test
    public void getCorrectAnswersCountShouldHaveNormalBehaviour() {
        when(phaseDao.findPhaseEntitiesByGameId(GAME_ID)).thenReturn(singletonList(new PhaseEntity()));

        final long actual = gameService.getCorrectAnswersCount(GAME);
        assertEquals(AMOUNT_OF_CORRECT_ANSWERS, actual);
    }

    @Test
    public void updateGameShouldThrowExceptionDueToNullGame() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Null game passed to update a game");

        gameService.updateGame(null);
    }

    @Test
    public void updateGameShouldHaveNormalBehaviour() {
        when(gameMapper.mapGameToGameEntity(GAME)).thenReturn(GAME_ENTITY);
        gameService.updateGame(GAME);

        verify(gameDao).save(GAME_ENTITY);
    }

    @Test
    public void findAllByTeamIdShouldThrowExceptionDueToNullGame() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Null id passed to find games by team id");

        gameService.findAllByTeamId(null, null);
    }

    private static GameEntity getGameEntity() {
        GameEntity gameEntity = new GameEntity();

        gameEntity.setId(GAME_ID);
        gameEntity.setNumberOfQuestions(NUMBER_OF_QUESTIONS);
        gameEntity.setTimePerQuestion(TIME_PER_QUESTION);
        gameEntity.setTeam(TEAM_ENTITY);
        gameEntity.setStatusEntity(StatusEntity.ONGOING);

        return gameEntity;
    }

    private static TeamEntity getTeamEntity() {
        TeamEntity teamEntity = new TeamEntity();

        teamEntity.setId(TEAM_ID);
        teamEntity.setTeamName(TEAM_NAME);

        return teamEntity;
    }

}