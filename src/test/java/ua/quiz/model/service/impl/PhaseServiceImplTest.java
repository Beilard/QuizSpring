package ua.quiz.model.service.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.quiz.model.dto.Game;
import ua.quiz.model.dto.Phase;
import ua.quiz.model.entity.PhaseEntity;
import ua.quiz.model.repository.PhaseRepository;
import ua.quiz.model.repository.QuestionRepository;
import ua.quiz.model.service.mapper.PhaseMapper;
import ua.quiz.model.service.mapper.QuestionMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PhaseServiceImpl.class)
public class PhaseServiceImplTest {
    private static final Long ID = 1L;

    private static final Long GAME_ID = 2L;

    private static final int DEFAULT_TIME_PER_QUESTION = 60;

    private static final String ANSWER = "Answer";


    private static final Phase PHASE_EXAMPLE = Phase.builder()
            .id(ID)
            .game(Game.builder()
                    .id(GAME_ID)
                    .build())
            .build();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private PhaseServiceImpl phaseService;
    @MockBean
    private PhaseRepository phaseDao;
    @MockBean
    private QuestionRepository questionRepository;
    @MockBean
    private PhaseMapper phaseMapper;
    @MockBean
    private QuestionMapper questionMapper;

    @Test
    public void initiatePhaseShouldThrowExceptionDueToNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Passed phase is null or timePerQuestion is invalid");

        phaseService.initiatePhase(null, null);
    }

    @Test
    public void initiatePhaseShouldHaveNormalBehaviour() {
        final PhaseEntity entity = new PhaseEntity();
        entity.setId(ID);
        entity.setStartTime(LocalDateTime.now());
        entity.setDeadline(LocalDateTime.now().plusSeconds(DEFAULT_TIME_PER_QUESTION));
        entity.setHintUsed(false);
        entity.setIsCorrect(false);

        when(phaseMapper.mapPhaseToPhaseEntity(any())).thenReturn(entity);
        phaseService.initiatePhase(PHASE_EXAMPLE, DEFAULT_TIME_PER_QUESTION);

        verify(phaseDao).save(any());
    }

    @Test
    public void finishPhaseShouldThrowExceptionDueToNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Passed phase is null");

        phaseService.finishPhase(null, null);
    }

    @Test
    public void finishPhaseShouldHaveNormalBehaviour() {
        final PhaseEntity entity = new PhaseEntity();
        entity.setId(ID);
        entity.setEndTime(LocalDateTime.now());
        entity.setGivenAnswer(ANSWER);

        when(phaseMapper.mapPhaseToPhaseEntity(any())).thenReturn(entity);
        phaseService.finishPhase(PHASE_EXAMPLE, ANSWER);

        verify(phaseDao).save(any());
    }

    @Test
    public void reviewPhasePositivelyShouldThrowExceptionDueToNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Passed phase to positively review is null");

        phaseService.reviewPhasePositively(null);
    }

    @Test
    public void reviewPhasePositivelyShouldHaveNormalBehaviour() {
        PhaseEntity entity = new PhaseEntity();
        entity.setIsCorrect(true);

        when(phaseMapper.mapPhaseToPhaseEntity(any())).thenReturn(entity);
        phaseService.reviewPhasePositively(PHASE_EXAMPLE);

        verify(phaseDao).save(entity);
    }

    @Test
    public void useHintShouldThrowExceptionDueToNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Passed phase to turn on hint is null");

        phaseService.useHint(null);
    }

    @Test
    public void useHintShouldHaveNormalBehaviour() {
        final PhaseEntity entity = new PhaseEntity();
        entity.setHintUsed(true);

        when(phaseMapper.mapPhaseToPhaseEntity(any())).thenReturn(entity);
        phaseService.useHint(PHASE_EXAMPLE);

        verify(phaseDao).save(any());
    }

}