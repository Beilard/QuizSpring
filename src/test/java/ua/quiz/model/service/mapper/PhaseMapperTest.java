package ua.quiz.model.service.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.quiz.model.dto.Phase;
import ua.quiz.model.entity.PhaseEntity;

import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PhaseMapper.class)
public class PhaseMapperTest {
    private static final Long ID = 0L;

    private static final LocalDateTime START_TIME = LocalDateTime.of(1995, 2, 17, 2, 2, 0);

    private static final LocalDateTime END_TIME = LocalDateTime.of(1995, 2, 17, 2, 3, 0);

    private static final LocalDateTime DEADLINE = LocalDateTime.of(1995, 2, 17, 2, 4, 0);

    private static final boolean HINT_USED = false;

    private static final boolean IS_CORRECT = true;

    private static final String GIVEN_ANSWER = "ANSWER";

    private static final Long GAME_ID = 1L;

    @Autowired
    private PhaseMapper phaseMapper;
    @MockBean
    private QuestionMapper questionMapper;
    @MockBean
    private GameMapper gameMapper;

    @Test
    public void mapPhaseEntityToPhaseShouldReturnPhase() {
        final PhaseEntity phaseEntity = new PhaseEntity();
        phaseEntity.setId(ID);
        phaseEntity.setStartTime(START_TIME);
        phaseEntity.setEndTime(END_TIME);
        phaseEntity.setDeadline(DEADLINE);
        phaseEntity.setHintUsed(HINT_USED);
        phaseEntity.setIsCorrect(IS_CORRECT);
        phaseEntity.setGivenAnswer(GIVEN_ANSWER);

        final Phase phase = phaseMapper.mapPhaseEntityToPhase(phaseEntity);
        assertThat("mapping id has failed", phase.getId(), is(ID));
        assertThat("mapping startTime has failed", phase.getStartTime(), is(START_TIME));
        assertThat("mapping endTime has failed", phase.getEndTime(), is(END_TIME));
        assertThat("mapping deadline has failed", phase.getDeadline(), is(DEADLINE));
        assertThat("mapping hintUsed has failed", phase.getHintUsed(), is(HINT_USED));
        assertThat("mapping isCorrect has failed", phase.getIsCorrect(), is(IS_CORRECT));
        assertThat("mapping givenAnswer has failed", phase.getGivenAnswer(), is(GIVEN_ANSWER));
    }

    @Test
    public void mapPhaseToPhaseEntityShouldReturnPhaseEntity() {
        final Phase phase = Phase.builder()
                .id(ID)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .deadline(DEADLINE)
                .hintUsed(HINT_USED)
                .isCorrect(IS_CORRECT)
                .givenAnswer(GIVEN_ANSWER)
                .build();

        final PhaseEntity phaseEntity = phaseMapper.mapPhaseToPhaseEntity(phase);
        assertThat("mapping id has failed", phaseEntity.getId(), is(ID));
        assertThat("mapping startTime has failed", phaseEntity.getStartTime(), is(START_TIME));
        assertThat("mapping endTime has failed", phaseEntity.getEndTime(), is(END_TIME));
        assertThat("mapping deadline has failed", phaseEntity.getDeadline(), is(DEADLINE));
        assertThat("mapping hintUsed has failed", phaseEntity.getHintUsed(), is(HINT_USED));
        assertThat("mapping isCorrect has failed", phaseEntity.getIsCorrect(), is(IS_CORRECT));
        assertThat("mapping givenAnswer has failed", phaseEntity.getGivenAnswer(), is(GIVEN_ANSWER));
    }

}