package ua.quiz.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class Question {
    private Long id;

    @NotEmpty(message = "Please, provide a question body")
    private String body;

    @NotEmpty(message = "Please, provide a correct answer")
    private String correctAnswer;

    @NotEmpty(message = "Please, provide a hint")
    private String hint;
}
