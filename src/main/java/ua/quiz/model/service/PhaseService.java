package ua.quiz.model.service;

import ua.quiz.model.dto.Phase;

public interface PhaseService {
    Phase initiatePhase(Phase phase, Integer timePerQuestion);

    Phase finishPhase(Phase phase, String givenAnswer);

    void reviewPhasePositively(Phase phase);

    Phase useHint(Phase phase);
}
