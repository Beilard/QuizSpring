package ua.quiz.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.quiz.model.dto.Game;
import ua.quiz.model.dto.Team;

import java.util.List;

public interface
GameService {
    Game startGame(Team team, int numberOfQuestions, int timePerQuestion);

    void finishGame(Game game);

    Game startReview(Game game);

    void finishReview(Game game);

    void updateGame(Game game);

    Game findById(Long id);

    Long getCorrectAnswersCount(Game game);

    Page<Game> findAll(Pageable pageable);

    Page<Game> findAllByTeamId(Long teamId, Pageable pageable);
}
