package ua.quiz.model.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.quiz.model.dto.Game;
import ua.quiz.model.dto.Phase;
import ua.quiz.model.dto.Question;
import ua.quiz.model.exception.EntityNotFoundException;
import ua.quiz.model.repository.PhaseRepository;
import ua.quiz.model.repository.QuestionRepository;
import ua.quiz.model.service.PhaseService;
import ua.quiz.model.service.mapper.PhaseMapper;
import ua.quiz.model.service.mapper.QuestionMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service("phaseService")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log4j
public class PhaseServiceImpl implements PhaseService {

    private final PhaseRepository phaseRepository;
    private final QuestionRepository questionRepository;
    private final PhaseMapper phaseMapper;
    private final QuestionMapper questionMapper;

    @Override
    public void generatePhaseList(Game game, Integer numberOfPhases) {
        if (game == null || numberOfPhases == null || numberOfPhases < 1) {
            log.warn("Game or number of phases passed to " +
                    "generate list is null or number of phases is less than 1");
            throw new IllegalArgumentException("Game or number of phases passed to " +
                    "generate list is null or number of phases is less than 1");
        }

        Long amountOfQuestionsInDb = questionRepository.count();
        if (numberOfPhases > amountOfQuestionsInDb) {
            log.warn("Amount of questions requested is bigger than the count in DB");
            throw new IllegalArgumentException("Amount of questions requested is bigger than the count in DB");
        }
        List<Long> generatedIds = generateIds(numberOfPhases, amountOfQuestionsInDb);

        for (int i = 0; i < numberOfPhases; i++) {
            final Phase phase = generatePhase(game, generatedIds, i);

            phaseRepository.save(phaseMapper.mapPhaseToPhaseEntity(phase));
        }
    }

    @Override
    public void initiatePhase(Phase phase, Integer timePerQuestion) {
        if (phase == null || timePerQuestion == null) {
            log.warn("Passed phase is null or timePerQuestion is invalid");
            throw new IllegalArgumentException("Passed phase is null or timePerQuestion is invalid");
        }
        Phase initiatedPhase = setDeadlines(phase, timePerQuestion);

        phaseRepository.save(phaseMapper.mapPhaseToPhaseEntity(initiatedPhase));
    }

    @Override
    public void finishPhase(Phase phase, String givenAnswer) {
        if (phase == null) {
            log.warn("Passed phase is null");
            throw new IllegalArgumentException("Passed phase is null");
        }
        final Phase finishedPhase = setEndTimeAndAnswer(phase, givenAnswer);
        phaseRepository.save(phaseMapper.mapPhaseToPhaseEntity(finishedPhase));
    }

    @Override
    public void reviewPhasePositively(Phase phase) {
        if (phase == null) {
            log.warn("Passed phase to positively review is null");
            throw new IllegalArgumentException("Passed phase to positively review is null");
        }
        Phase updatedPhase = changeToCorrect(phase);
        phaseRepository.save(phaseMapper.mapPhaseToPhaseEntity(updatedPhase));
    }

    @Override
    public List<Phase> findPhasesByGameId(Long gameId) {
        if (gameId == null) {
            log.warn("Null gameId passed to find list of phases");
            throw new IllegalArgumentException("Null gameId passed to find list of phases");
        }

        return phaseRepository.findPhaseEntitiesByGameId(gameId)
                .stream()
                .map(phaseMapper::mapPhaseEntityToPhase)
                .collect(Collectors.toList());
    }

    @Override
    public void useHint(Phase phase) {
        if (phase == null) {
            log.warn("Passed phase to turn on hint is null");
            throw new IllegalArgumentException("Passed phase to turn on hint is null");
        }
        Phase phaseWithHint = enableHint(phase);
        phaseRepository.save(phaseMapper.mapPhaseToPhaseEntity(phaseWithHint));
    }

    private Phase setDeadlines(Phase phase, Integer timePerQuestion) {
        return phase.toBuilder()
                .startTime(LocalDateTime.now())
                .deadline(LocalDateTime.now().plusSeconds(timePerQuestion))
                .hintUsed(false)
                .isCorrect(false)
                .build();
    }

    private Phase setEndTimeAndAnswer(Phase phase, String givenAnswer) {
        return phase.toBuilder()
                .endTime(LocalDateTime.now())
                .givenAnswer(givenAnswer)
                .build();
    }

    private Phase enableHint(Phase phase) {
        return phase.toBuilder()
                .hintUsed(true)
                .build();
    }

    private Phase changeToCorrect(Phase phase) {
        return phase.toBuilder()
                .isCorrect(true)
                .build();
    }

    private Phase generatePhase(Game game, List<Long> generatedIds, int i) {
        final Question question = questionRepository.findById(generatedIds.get(i))
                .map(questionMapper::mapQuestionEntityToQuestion)
                .orElseThrow(EntityNotFoundException::new);

        return Phase.builder()
                .game(game)
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
}
