package ua.quiz.model.service.mapper;

import ua.quiz.model.dto.Phase;
import ua.quiz.model.dto.Question;
import ua.quiz.model.entity.PhaseEntity;

import java.util.Objects;

public class PhaseMapper {
    private final QuestionMapper questionMapper = new QuestionMapper();

    public Phase mapPhaseEntityToPhase(PhaseEntity phaseEntity) {
        QuestionMapper questionMapper = new QuestionMapper();

        if (Objects.isNull(phaseEntity)) {
            return null;
        }
        return Phase.builder()
                .id(phaseEntity.getId())
                .question(questionMapper.mapQuestionEntityToQuestion(phaseEntity.getQuestion()))
                .startTime(phaseEntity.getStartTime())
                .endTime(phaseEntity.getStartTime())
                .deadline(phaseEntity.getDeadline())
                .hintUsed(phaseEntity.getHintUsed())
                .isCorrect(phaseEntity.getIsCorrect())
                .givenAnswer(phaseEntity.getGivenAnswer())
                .gameId(phaseEntity.getGame().getId())
                .build();
    }

    public PhaseEntity mapPhaseToPhaseEntity(Phase phase) {
        if (Objects.isNull(phase)) {
            return null;
        }

        PhaseEntity entity = new PhaseEntity();

        entity.setId(phase.getId());
        entity.setQuestion(questionMapper.mapQuestionToQuestionEntity(phase.getQuestion()));
        entity.setStartTime(phase.getStartTime());
        entity.setEndTime(phase.getEndTime());
        entity.setDeadline(phase.getDeadline());
        entity.setHintUsed(phase.getHintUsed());
        entity.setIsCorrect(phase.getIsCorrect());
        entity.setGivenAnswer(phase.getGivenAnswer());

        return entity;
    }

}
