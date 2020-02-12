package ua.quiz.model.service.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.quiz.model.dto.Game;
import ua.quiz.model.dto.Phase;
import ua.quiz.model.dto.Status;
import ua.quiz.model.dto.Team;
import ua.quiz.model.entity.GameEntity;
import ua.quiz.model.entity.PhaseEntity;
import ua.quiz.model.entity.StatusEntity;
import ua.quiz.model.entity.TeamEntity;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = GameMapper.class)
public class GameMapperTest {
    private static final Long ID = 0L;

    private static final int NUMBER_OF_QUESTIONS = 3;

    private static final int TIME_PER_QUESTION = 5;

    private static final Status STATUS = Status.ONGOING;

    private static final StatusEntity STATUS_ENTITY = StatusEntity.ONGOING;

    private static final Team TEAM = new Team(1L, "Name");

    private static final TeamEntity TEAM_ENTITY = getTeamEntity();

    @Autowired
    private  GameMapper gameMapper;
    @MockBean
    private TeamMapper teamMapper;

    @Test
    public void mapGameEntityToGameShouldReturnGame() {
        Mockito.when(teamMapper.mapTeamEntityToTeam(TEAM_ENTITY)).thenReturn(TEAM);

        final GameEntity gameEntity = new GameEntity();

        gameEntity.setId(ID);
        gameEntity.setNumberOfQuestions(NUMBER_OF_QUESTIONS);
        gameEntity.setTimePerQuestion(TIME_PER_QUESTION);
        gameEntity.setTeam(TEAM_ENTITY);
        gameEntity.setStatusEntity(StatusEntity.valueOf(STATUS.name()));

        final Game game = gameMapper.mapGameEntityToGame(gameEntity);
        assertThat("mapping id has failed", game.getId(), is(ID));
        assertThat("mapping number of questions has failed", game.getNumberOfQuestions(), is(NUMBER_OF_QUESTIONS));
        assertThat("mapping time per question has failed", game.getTimePerQuestion(), is(TIME_PER_QUESTION));
        assertThat("mapping teamId has failed", game.getTeam(), is(TEAM));
        assertThat("mapping status has failed", game.getStatus(), is(STATUS));
    }


    @Test
    public void mapGameToGameEntityShouldReturnGameEntity() {
        Mockito.when(teamMapper.mapTeamToTeamEntity(TEAM)).thenReturn(TEAM_ENTITY);

        final Game game = Game.builder()
                .id(ID)
                .numberOfQuestions(NUMBER_OF_QUESTIONS)
                .timePerQuestion(TIME_PER_QUESTION)
                .team(TEAM)
                .status(Status.valueOf(STATUS.name()))
                .build();

        final GameEntity gameEntity = gameMapper.mapGameToGameEntity(game);
        assertThat("mapping id has failed", gameEntity.getId(), is(ID));
        assertThat("mapping number of questions has failed", gameEntity.getNumberOfQuestions(), is(NUMBER_OF_QUESTIONS));
        assertThat("mapping time per question has failed", gameEntity.getTimePerQuestion(), is(TIME_PER_QUESTION));
        assertThat("mapping teamId has failed", gameEntity.getTeam(), is(TEAM_ENTITY));
        assertThat("mapping status has failed", gameEntity.getStatusEntity(), is(STATUS_ENTITY));
    }

    private static TeamEntity getTeamEntity() {
        TeamEntity teamEntity = new TeamEntity();

        teamEntity.setTeamName("Name");
        teamEntity.setId(1L);

        return teamEntity;
    }

}