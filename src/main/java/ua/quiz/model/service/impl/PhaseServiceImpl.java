package ua.quiz.model.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.quiz.model.dto.Phase;
import ua.quiz.model.repository.PhaseRepository;
import ua.quiz.model.service.PhaseService;
import ua.quiz.model.service.mapper.PhaseMapper;

import java.time.LocalDateTime;

@Service("phaseService")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log4j
public class PhaseServiceImpl implements PhaseService {

    private final PhaseRepository phaseRepository;
    private final PhaseMapper phaseMapper;


    @Override
    public Phase initiatePhase(Phase phase, Integer timePerQuestion) {
        if (phase == null) {
            log.warn("Passed phase is null");
            throw  new IllegalArgumentException("Passed phase is null");
        }
        Phase initiatedPhase = setDeadlines(phase, timePerQuestion);

        phaseRepository.save(phaseMapper.mapPhaseToPhaseEntity(initiatedPhase));

        return initiatedPhase;
    }

    @Override
    public Phase finishPhase(Phase phase, String givenAnswer) {
        if (phase == null) {
            log.warn("Passed phase is null");
            throw  new IllegalArgumentException("Passed phase is null");
        }
        final Phase finishedPhase = setEndTimeAndAnswer(phase, givenAnswer);
        phaseRepository.save(phaseMapper.mapPhaseToPhaseEntity(finishedPhase));

        return finishedPhase;
    }

    @Override
    public void reviewPhasePositively(Phase phase) {
        if (phase == null) {
            log.warn("Passed phase is null");
            throw  new IllegalArgumentException("Passed phase is null");
        }
        Phase updatedPhase = changeToCorrect(phase);
        phaseRepository.save(phaseMapper.mapPhaseToPhaseEntity(updatedPhase));
    }

    @Override
    public Phase useHint(Phase phase) {
        if (phase == null) {
            log.warn("Passed phase is null");
            throw new IllegalArgumentException("Passed phase is null");
        }
        Phase phaseWithHint = enableHint(phase);
        phaseRepository.save(phaseMapper.mapPhaseToPhaseEntity(phaseWithHint));

        return phaseWithHint;
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
}
