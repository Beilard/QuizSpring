package ua.quiz.model.service;

import ua.quiz.model.dto.Game;
import ua.quiz.model.dto.Phase;

import java.util.List;

public interface PhaseService {
    void generatePhaseList(Game game, Integer numberOfPhases);

    void initiatePhase(Phase phase, Integer timePerQuestion);

    List<Phase> findPhasesByGameId(Long gameId);

    void finishPhase(Phase phase, String givenAnswer);

    void reviewPhasePositively(Phase phase);

    void useHint(Phase phase);
}
