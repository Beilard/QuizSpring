package ua.quiz.model.service.mapper;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.quiz.model.dto.Phase;
import ua.quiz.model.entity.GameEntity;
import ua.quiz.model.entity.PhaseEntity;
import ua.quiz.model.repository.GameRepository;

import java.util.Objects;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PhaseMapper {
    private final QuestionMapper questionMapper = new QuestionMapper();

    private GameRepository gameRepository;

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
        GameEntity gameEntity = new GameEntity();
        gameEntity.setId(phase.getId());

        entity.setId(phase.getId());
        entity.setQuestion(questionMapper.mapQuestionToQuestionEntity(phase.getQuestion()));
        entity.setStartTime(phase.getStartTime());
        entity.setEndTime(phase.getEndTime());
        entity.setDeadline(phase.getDeadline());
        entity.setHintUsed(phase.getHintUsed());
        entity.setIsCorrect(phase.getIsCorrect());
        entity.setGivenAnswer(phase.getGivenAnswer());
        entity.setGame(gameRepository.findById(phase.getGameId()).orElseThrow(IllegalArgumentException::new));

        return entity;
    }

}
