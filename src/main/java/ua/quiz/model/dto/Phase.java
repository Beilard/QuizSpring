package ua.quiz.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder=true)
public class Phase {
    private Long id;

    @NotEmpty(message = "Please, provide a question")
    private Question question;

    @NotEmpty(message = "Please, provide a hint")
    private Boolean hintUsed;

    @NotEmpty(message = "Please, provide if it is correct")
    private Boolean isCorrect;
    private String givenAnswer;

    @NotEmpty(message = "Please, provide a game")
    private Game game;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime deadline;
}
