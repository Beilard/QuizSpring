package ua.quiz.model.service;

import ua.quiz.model.dto.Game;
import ua.quiz.model.dto.Phase;

import java.util.List;

public interface PhaseService {
    List<Phase> generatePhaseList(Game game, Integer numberOfPhases);

    Phase initiatePhase(Phase phase, Integer timePerQuestion);

    List<Phase> findPhasesByGameId(Long gameId);

    Phase finishPhase(Phase phase, String givenAnswer);

    void reviewPhasePositively(Phase phase);

    Phase useHint(Phase phase);
}
