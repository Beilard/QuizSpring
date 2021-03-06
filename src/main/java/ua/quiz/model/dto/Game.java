package ua.quiz.model.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder(toBuilder=true)
public class Game {
    private Long id;

    @NotEmpty(message = "Please, provide a number of questions")
    private Integer numberOfQuestions;

    @NotEmpty(message = "Please, provide time per question")
    private Integer timePerQuestion;

    @NotEmpty(message = "Please, provide current phase number")
    private Integer currentPhase;

    @NotEmpty(message = "Please, provide a teamId")
    private Team team;

    @NotEmpty(message = "Please, provide status")
    private Status status;
}
