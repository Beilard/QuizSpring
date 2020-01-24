package ua.quiz.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
public class Team {
    private Long id;

    @NotEmpty(message = "Please, provide a team name")
    private String teamName;
}
