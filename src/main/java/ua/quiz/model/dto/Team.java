package ua.quiz.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class Team {
    private Long id;

    @NotEmpty(message = "Please, provide a team name")
    private String teamName;
}
