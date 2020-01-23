package ua.quiz.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.quiz.model.entity.GameEntity;

import java.util.List;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<GameEntity, Long> {
    List<GameEntity> findAllByTeamId(Long teamId, Long startFrom, Long countRow);

    Long countAllByTeamId(Long teamId);
}
