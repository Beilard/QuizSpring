package ua.quiz.model.service;

import ua.quiz.model.dto.Game;

import java.util.List;

public interface GameService {
    Game startGame(Long teamId, int numberOfQuestions, int timePerQuestion);

    void finishGame(Game game);

    Game startReview(Game game);

    void finishReview(Game game);

    void updateGame(Game game);

    Game findById(Long id);

    Long getCorrectAnswersCount(Game game);

    List<Game> findAll(Integer page, Integer rowCount);

    List<Game> findAllByTeamId(Long teamId, Integer page, Integer rowCount);

    Long countAllEntries();

    Long countAllByTeamId(Long teamId);
}
