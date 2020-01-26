package ua.quiz.model.service.mapper;

import org.springframework.stereotype.Component;
import ua.quiz.model.dto.Question;
import ua.quiz.model.entity.QuestionEntity;

import java.util.Objects;

@Component
public class QuestionMapper {
    public Question mapQuestionEntityToQuestion(QuestionEntity entity) {
        return Objects.isNull(entity) ? null : Question.builder()
                .id(entity.getId())
                .body(entity.getBody())
                .correctAnswer(entity.getCorrectAnswer())
                .hint(entity.getHint())
                .build();
    }

    public QuestionEntity mapQuestionToQuestionEntity(Question question) {
        if (Objects.isNull(question)) {
            return null;
        }

        QuestionEntity entity = new QuestionEntity();
        entity.setId(question.getId());
        entity.setBody(question.getBody());
        entity.setCorrectAnswer(question.getCorrectAnswer());
        entity.setHint(question.getHint());

        return entity;
    }
}
