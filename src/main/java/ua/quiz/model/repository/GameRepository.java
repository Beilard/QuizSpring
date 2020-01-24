package ua.quiz.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.quiz.model.entity.GameEntity;

import java.awt.print.Pageable;
import java.util.List;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<GameEntity, Long> {
    List<GameEntity> findByTeamId(Long teamId, Pageable page);

    Long countAllByTeamId(Long teamId);
}
